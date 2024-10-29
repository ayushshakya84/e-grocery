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

    environment {
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')
        AWS_DEFAULT_REGION = 'ap-south-1'
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/"
    }

    stages {
        stage('Order Service Build') {
            when {
                expression { sh(script: "git diff --name-only HEAD~1 HEAD | grep '^order/'", returnStatus: true) == 0 }
            }
            stages {
                stage('Package Build') {
                    steps {
                        container('maven') {
                            dir('order') {
                                sh 'mvn clean package'
                            }
                        }
                    }
                }

                stage('Docker Build & Push') {
                    steps {
                        container('docker') {
                            script {
                                sh """
                                docker build -t ${REPOSITORY_URI}order:${BUILD_NUMBER} order/
                                docker push ${REPOSITORY_URI}order:${BUILD_NUMBER}
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Payment Service Build') {
            when {
                expression { sh(script: "git diff --name-only HEAD~1 HEAD | grep '^payment/'", returnStatus: true) == 0 }
            }
            stages {
                stage('Package Build') {
                    steps {
                        container('maven') {
                            dir('payment') {
                                sh 'mvn clean package'
                            }
                        }
                    }
                }

                stage('Docker Build & Push') {
                    steps {
                        container('docker') {
                            script {
                                sh """
                                docker build -t ${REPOSITORY_URI}payment:${BUILD_NUMBER} payment/
                                docker push ${REPOSITORY_URI}payment:${BUILD_NUMBER}
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Inventory Service Build') {
            when {
                expression { sh(script: "git diff --name-only HEAD~1 HEAD | grep '^inventory/'", returnStatus: true) == 0 }
            }
            stages {
                stage('Package Build') {
                    steps {
                        container('maven') {
                            dir('inventory') {
                                sh 'mvn clean package'
                            }
                        }
                    }
                }

                stage('Docker Build & Push') {
                    steps {
                        container('docker') {
                            script {
                                sh """
                                docker build -t ${REPOSITORY_URI}inventory:${BUILD_NUMBER} inventory/
                                docker push ${REPOSITORY_URI}inventory:${BUILD_NUMBER}
                                """
                            }
                        }
                    }
                }
            }
        }

        // Add more stages for additional services if needed
    }
}
