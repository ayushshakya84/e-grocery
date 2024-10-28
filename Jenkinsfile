pipeline {
    agent {
        kubernetes {
            yaml '''
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
            '''
        }
    }
    environment  {
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')
        AWS_ECR_REPO_NAME = credentials('ecr-e-grocery-order')
        AWS_DEFAULT_REGION = 'ap-south-1'
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/"
    }
    stages {
        stage('Package Build') {
            when {
                expression {
                    return sh(script: 'git diff --name-only HEAD~1 HEAD | grep "^order/"', returnStatus: true) == 0
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
                        dir('order/') {
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
                    return sh(script: 'git diff --name-only HEAD~1 HEAD | grep "^order/"', returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    script {
                        dir('order/') {
                            sh 'dockerd &'
                            sh 'sleep 2'
                            sh 'docker system prune -f'
                            sh 'docker container prune -f'
                            sh 'docker build -t ${AWS_ECR_REPO_NAME} .'
                        }
                    }
                }
            }
        }
        stage("ECR Image Pushing") {
            when {
                expression {
                    return sh(script: 'git diff --name-only HEAD~1 HEAD | grep "^order/"', returnStatus: true) == 0
                }
            }
            steps {
                container('docker') {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-cred', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        script {
                            sh 'aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${REPOSITORY_URI}'
                            sh 'docker tag ${AWS_ECR_REPO_NAME} ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER}'
                            sh 'docker push ${REPOSITORY_URI}${AWS_ECR_REPO_NAME}:${BUILD_NUMBER}'
                        }
                    }
                }
            }
        }
    }
}
