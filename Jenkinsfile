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
                image: ayush8410/docker-aws-trivy:v1
                tty: true
                securityContext:
                  privileged: true
              - name: maven
                image: maven:3.8.5-openjdk-17
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
                    env.AWS_ECR_REPO_NAME = "ecr-e-grocery-${APP_DIR}" 
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
            steps {
                container('maven') {
                    dir("${env.WORKSPACE}/${env.APP_DIR}") {
                        withSonarQubeEnv('sonar-server') {
                            sh ''' 
                            java -version
                            $SCANNER_HOME/bin/sonar-scanner \
                            -Dsonar.organization=ayushshakya84 \
                            -Dsonar.projectName=e-grocery-${APP_DIR} \
                            -Dsonar.projectKey=ayushshakya84_e-grocery-${APP_DIR}
                            '''
                        }
                    }
                }
            }
        }

        stage('Quality Check') {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${APP_DIR}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('maven') {
                    script {
                        waitForQualityGate abortPipeline: false, credentialsId: 'sonar-token'
                    }
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
    }
}
