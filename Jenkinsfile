pipeline {
    agent none  // Disable global agent as each parallel branch will use its own pod

    environment  {
        SCANNER_HOME = tool 'sonar-scanner'
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')
        AWS_DEFAULT_REGION = 'ap-south-1'
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/"
    }

    stages {
        stage('Determine Changed App Directories') {
            agent any
            steps {
                script {
                    def changedDirs = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim().tokenize('\n').collect { it.split('/')[0] }.unique()
                    env.CHANGED_DIRS = changedDirs.findAll { it in ['gateway', 'notification', 'order', 'payment', 'product', 'profile', 'search', 'shipment'] }.join(',')
                    echo "Detected changed directories: ${env.CHANGED_DIRS}"
                }
            }
        }

        stage('Build and Deploy Applications') {
            steps {
                script {
                    def appDirs = env.CHANGED_DIRS.tokenize(',')
                    def parallelTasks = [:]

                    appDirs.each { appDir ->
                        parallelTasks["${appDir} Pipeline"] = {
                            runAppPipeline(appDir)
                        }
                    }

                    // Execute all tasks in parallel, each in its own pod
                    parallel parallelTasks
                }
            }
        }
    }
}

def runAppPipeline(appDir) {
    podTemplate(containers: [
        containerTemplate(name: 'docker', image: 'ayush8410/docker-aws-trivy:v1', ttyEnabled: true, privileged: true),
        containerTemplate(name: 'maven', image: 'maven:3.6.3-jdk-11', command: 'cat', ttyEnabled: true)
    ],
    serviceAccount: 'jenkins') {
        node(POD_LABEL) {
            env.APP_DIR = appDir
            env.AWS_ECR_REPO_NAME = "e-grocery/${appDir}"
            
            stage("Package Build - ${appDir}") {
                container('maven') {
                    dir("lib/") {
                        sh '''
                        echo "Installing Dependencies for ${appDir} service"
                        bash script.sh
                        '''
                    }
                    dir("${env.WORKSPACE}/${appDir}") {
                        sh '''
                        echo "Building package for ${appDir} service"
                        mvn clean package
                        '''
                    }
                }
            }

            stage("Sonarqube Analysis - ${appDir}") {
                container('maven') {
                    dir("${env.WORKSPACE}/${appDir}") {
                        withSonarQubeEnv('sonar-server') {
                            sh '''
                            mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=ayushshakya84_e-grocery-${appDir}
                            '''
                        }
                    }
                }
            }

            stage("Trivy File Scan - ${appDir}") {
                container('docker') {
                    dir("${env.WORKSPACE}/${appDir}") {
                        sh 'trivy fs . > trivyfs.txt'
                    }
                }
            }

            stage("Docker Image Build - ${appDir}") {
                container('docker') {
                    dir("${env.WORKSPACE}/${appDir}") {
                        sh '''
                        echo "Building Image for ${appDir} service"
                        dockerd &
                        sleep 2
                        docker system prune -f
                        docker container prune -f
                        docker build -t ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER} .
                        '''
                    }
                }
            }

            stage("TRIVY Image Scan - ${appDir}") {
                container('docker') {
                    sh 'trivy image ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER} > trivyimage.txt'
                }
            }

            stage("ECR Image Pushing - ${appDir}") {
                container('docker') {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-cred', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh '''
                        aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${REPOSITORY_URI}
                        docker push ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER}
                        '''
                    }
                }
            }
        }
    }
}
