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
                image: maven:3.6.3-jdk-11
                command:
                - cat
                tty: true
            """
        }
    }
    
    environment  {
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')
        AWS_ECR_REPO_NAME = credentials('ecr-e-grocery-order')
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
                    echo "Detected application directory: ${APP_DIR}"
                }
            }
        }

        stage('Package Build') {
            when {
                expression {
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${JOB_NAME}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('maven') {
                    script {
                        dir('lib/') {
                            sh '''
                            bash script.sh
                            '''
                        }
                        dir("${env.WORKSPACE}/${env.APP_DIR}") {
                            sh ''' 
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
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${JOB_NAME}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    script {
                        dir("${env.WORKSPACE}/${env.APP_DIR}") {
                            sh """ 
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
                    return sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${JOB_NAME}/'", returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-cred', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        script {
                            sh """ 
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
