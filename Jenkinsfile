pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              serviceAccountName: jenkins
              containers:
              - name: kaniko
                image: gcr.io/kaniko-project/executor:debug
                tty: true
                command:
                - sleep
                args:
                - 99d
                volumeMounts:
                - name: kaniko-cache
                  mountPath: /cache
                - name: gcp-key
                  mountPath: /secret
                  readOnly: true
              - name: maven
                image: maven:3.6.3-jdk-11
                command:
                - sleep
                args:
                - 99d
                tty: true
                volumeMounts:
                - name: maven-cache
                  mountPath: /root/.m2
              - name: gcloud
                image: gcr.io/google.com/cloudsdktool/google-cloud-cli:latest
                command:
                - sleep
                args:
                - 99d
                tty: true
                volumeMounts:
                - name: gcp-key
                  mountPath: /secret
                  readOnly: true
              - name: trivy
                image: aquasec/trivy:latest
                command:
                - sleep
                args:
                - 99d
                tty: true
                volumeMounts:
                - name: gcp-key
                  mountPath: /secret
                  readOnly: true
              resources:
                limits:
                  memory: "2Gi"
                  cpu: "1"
                requests:
                  memory: "1Gi"
                  cpu: "500m"
              volumes:
                - name: kaniko-cache
                  emptyDir: {}
                - name: maven-cache
                  emptyDir: {}
                - name: gcp-key
                  secret:
                    secretName: gcp-service-account-key
            """
        }
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        GCP_PROJECT_ID = credentials('GCP_PROJECT_ID')
        GCP_REGION = 'asia-south1'
        REGISTRY_HOST = 'asia-south1-docker.pkg.dev'
        REPOSITORY_URI = "${REGISTRY_HOST}/${GCP_PROJECT_ID}/e-grocery-repo"
        SONAR_PROJECT_KEY = "ayushshakya84_e-grocery-${appDir}_${BUILD_NUMBER}"
        GIT_REPO_NAME = "e-grocery-k8s-infra"
        GIT_USER_NAME = "ayushshakya84"
        GIT_USER_EMAIL = "ayushshakya8410@gmail.com"
        GIT_BRANCH = "main"
        UPDATE_DIR = "e-grocery-k8s-infra"
        GOOGLE_APPLICATION_CREDENTIALS = "/secret/gcp-service-account-key.json"
    }

    stages {
        stage('Determine App Directories') {
            steps {
                script {
                    def config = readYaml file: 'config.yaml'
                    def application = config.application
                    def changedDirs = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true)
                                      .trim()
                                      .tokenize('\n')
                                      .collect { it.split('/')[0] }
                                      .unique()
                                      .findAll { application.contains(it) }
                    env.CHANGED_DIRS = changedDirs.join(',')
                    echo "Detected changed directories: ${env.CHANGED_DIRS}"
                }
            }
        }

        stage('Process Applications') {
            steps {
                script {
                    def appDirs = env.CHANGED_DIRS.tokenize(',')
                    def config = readYaml file: 'config.yaml'
                    def mavenBuildCommand = config.maven.build
                    def trivy_file_scan = config.trivy.file_scan
                    def trivy_image_scan = config.trivy.image_scan
                    if (appDirs.isEmpty()) {
                        echo "No relevant directories changed. Skipping builds."
                        return
                    }
                    parallel appDirs.collectEntries { appDir ->
                        ["${appDir} Pipeline": { runAppStages(appDir, mavenBuildCommand, trivy_file_scan, trivy_image_scan) }]
                    }
                }
            }
        }
    }
}

def runAppStages(appDir, mavenBuildCommand, trivy_file_scan, trivy_image_scan) {
    
    stage("Package Build - ${appDir}") {
        container('maven') {
            script{
                dir('lib/') {
                    sh '''
                    echo "Installing Dependencies for ${APP_DIR} service"
                    bash script.sh
                    '''
                }
                dir("${appDir}") {
                    sh """
                    echo "Building package for ${appDir} service"
                    ${mavenBuildCommand}
                    """
                }
            }
        }
    }

    // stage("SonarQube Analysis - ${appDir}") {
    //     container('maven') {
    //         dir("${appDir}") {
    //             withSonarQubeEnv('sonar-server') {
    //                 sh """
    //                 mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
    //                   -Dsonar.projectKey=${SONAR_PROJECT_KEY}
    //                 """
    //             }
    //         }
    //     }
    // }

    stage("Trivy File Scan - ${appDir}") {
        container('trivy') {
            dir("${appDir}") {
                sh """
                echo "Running Trivy filesystem scan"
                trivy fs . --format table --output trivy-fs-report.txt || echo "Trivy scan completed with issues"
                """
                archiveArtifacts artifacts: '**/trivy*.txt', allowEmptyArchive: true
            }
        }
    }

    stage("Docker Image Build & Push - ${appDir}") {
        container('kaniko') {
            dir("${appDir}") {
                script {
                    def imageTag = "${BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
                    env.IMAGE_TAG = imageTag
                    
                    sh """
                    echo "Building and pushing Docker image for ${appDir} service using Kaniko"
                    /kaniko/executor \
                      --context . \
                      --dockerfile Dockerfile \
                      --destination ${REPOSITORY_URI}/${appDir}:${imageTag} \
                      --cache=true \
                      --cache-dir=/cache \
                      --skip-tls-verify=false
                    """
                }
            }
        }
    }

    stage("Trivy Image Scan - ${appDir}") {
        container('trivy') {
            dir("${appDir}") {
                sh """
                echo "Authenticating with GCP for image scanning"
                gcloud auth activate-service-account --key-file=\${GOOGLE_APPLICATION_CREDENTIALS}
                gcloud auth configure-docker \${REGISTRY_HOST}
                
                echo "Running Trivy image scan"
                trivy image ${REPOSITORY_URI}/${appDir}:${IMAGE_TAG} --format table --output trivy-image-report.txt || echo "Image scan completed with issues"
                """
                archiveArtifacts artifacts: '**/trivy*.txt', allowEmptyArchive: true
            }
        }
    }

    stage("Verify Image Push - ${appDir}") {
        container('gcloud') {
            sh """
            echo "Verifying image push to Google Artifact Registry"
            gcloud auth activate-service-account --key-file=\${GOOGLE_APPLICATION_CREDENTIALS}
            gcloud config set project \${GCP_PROJECT_ID}
            gcloud artifacts docker images list ${REGISTRY_HOST}/${GCP_PROJECT_ID}/e-grocery-repo/${appDir} --limit=5
            """
        }
    }

    stage("Update Deployment Changes - ${appDir}") {
        container('gcloud') {
            script {
                // Check if the current appDir has changes
                def hasChanges = sh(
                    script: "git diff --name-only HEAD~1 HEAD | grep '^${appDir}/' || true",
                    returnStdout: true
                ).trim()
                
                if (hasChanges) {
                    cleanWs()
                    dir("${env.WORKSPACE}/${env.UPDATE_DIR}") {
                        withCredentials([string(credentialsId: 'GIT_TOKEN', variable: 'GITHUB_TOKEN')]) {
                            sh """
                            git clone https://\${GITHUB_TOKEN}@github.com/\${GIT_USER_NAME}/\${GIT_REPO_NAME}.git .
                            git config user.email \${GIT_USER_EMAIL}
                            git config user.name \${GIT_USER_NAME}
                            
                            # Update the image tag in values.yaml
                            yq -i ".image.tag = \\"\${IMAGE_TAG}\\"" main-app-values/${appDir}/values.yaml
                            
                            # Commit and push changes
                            git add main-app-values/${appDir}/values.yaml
                            git commit -m "Update ${appDir} deployment image to version \${IMAGE_TAG}"
                            git push origin \${GIT_BRANCH}
                            """
                        }
                    }
                } else {
                    echo "No changes detected for ${appDir}, skipping deployment update"
                }
            }
        }
    }
}
