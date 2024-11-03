pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              serviceAccountName: jenkins
              containers:
              - name: docker
                image: ayush8410/docker-aws-trivy:v2
                tty: true
                securityContext:
                  privileged: true
              - name: maven
                image: maven:3.6.3-jdk-11
                command:
                - cat
                tty: true
            """
        }
    }
    
    environment  {
        SCANNER_HOME=tool 'sonar-scanner'
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')
        AWS_DEFAULT_REGION = 'ap-south-1'
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/"
    }


    stages {
        stage('Determine App Directory') {
            steps {
                script {
                    def changedDirs = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim().tokenize('\n').collect { it.split('/')[0] }.unique()
                    APP_DIR = changedDirs.find { it in ['gateway', 'notification', 'order', 'odersaga', 'payment', 'product', 'profile', 'search', 'shipment'] }
                    env.APP_DIR = APP_DIR // Set the environment variable
                    env.AWS_ECR_REPO_NAME = "e-grocery/${APP_DIR}" 
                    echo "Detected application directory: ${APP_DIR}"
                    echo "${AWS_ECR_REPO_NAME}"
                }
            }
        }

        stage('Package Build') {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${APP_DIR}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('maven') {
                    script {
                        dir('lib/') {
                            sh '''
                            echo "Installing Dependencies for ${APP_DIR} service"
                            bash script.sh
                            '''
                        }
                        dir("${env.WORKSPACE}/${env.APP_DIR}") {
                            sh ''' 
                            echo "Building package for ${APP_DIR} service"
                            mvn clean package
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Sonarqube Analysis') {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${APP_DIR}/'", returnStatus: true) == 0
                }
            }
            environment {
                JAVA_HOME = "${tool 'jdk'}"
                PATH = "${JAVA_HOME}/bin:${env.PATH}"
            }
            steps {
                container('maven') {
                    dir("${env.WORKSPACE}/${env.APP_DIR}") {
                        withSonarQubeEnv('sonar-server') {
                            sh ''' 
                            mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=ayushshakya84_e-grocery-${APP_DIR}
                            '''
                        }
                    }
                }
            }
        }

        stage('Trivy File Scan') {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${APP_DIR}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    dir("${env.WORKSPACE}/${env.APP_DIR}") {
                        sh 'trivy fs . > trivyfs.txt'
                        sh 'yq'
                    }
                }
            }
        }

        stage("Docker Image Build") {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${APP_DIR}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    script {
                        dir("${env.WORKSPACE}/${env.APP_DIR}") {
                            sh """ 
                            echo "Building Image for ${APP_DIR} service"
                            dockerd &
                            sleep 2
                            docker system prune -f
                            docker container prune -f
                            docker build -t ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER} .
                            """
                        }
                    }
                }
            }
        }

        stage("TRIVY Image Scan") {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${APP_DIR}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    sh 'trivy image ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER} > trivyimage.txt'
                }
            }
        }

        stage("ECR Image Pushing") {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${APP_DIR}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-cred', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        script {
                            sh """
                            echo "Pushing Image of ${APP_DIR} service"
                            aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${REPOSITORY_URI}
                            docker push ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER}
                            """
                        }
                    }
                }
            }
        }

        stage('Update Deployment file') {
            environment {
                GIT_REPO_NAME = "e-grocery-k8s-infra"
                GIT_USER_NAME = "ayushshakya84"
                GIT_USER_EMAIL = "ayushshakya8410@gmail.com"
            }
            steps {
                container('docker') {
                    dir("${env.WORKSPACE}/${env.APP_DIR}") {
                        withCredentials([string(credentialsId: 'GIT_TOKEN', variable: 'GITHUB_TOKEN')]) {
                            sh '''
                                git config --global --add safe.directory /home/jenkins/agent/workspace/${JOB_NAME}
                                git config user.email ${GIT_USER_EMAIL}
                                git config user.name ${GIT_USER_NAME}
                                BUILD_NUMBER=${BUILD_NUMBER}
                                echo $BUILD_NUMBER
                                git pull --rebase https://${GITHUB_TOKEN}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME} ${BRANCH_NAME} || git rebase --abort
                             // sed -i "s/${AWS_ECR_REPO_NAME}:${imageTag}/${AWS_ECR_REPO_NAME}:${BUILD_NUMBER}/" deployment.yaml
                                yq e -i '.image.tag = env(BUILD_NUMBER)' ${APP_DIR}/values.yaml 
                                git add deployment.yaml
                                git commit -m "Update deployment Image to version \${BUILD_NUMBER}"
                                git push https://${GITHUB_TOKEN}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME} HEAD:${BRANCH_NAME}
                                echo $?
                            '''
                        }
                    }
                }
            }
        }
    }
}
