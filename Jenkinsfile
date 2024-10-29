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
        stage('Order Service') {
            when {
               changeset "**/order/*.*"
                beforeAgent true
            }
            stages {
                stage('Build Order Service') {
                    steps {
                        container('maven') {
                            dir('order') {
                                sh 'mvn clean package'
                            }
                        }
                    }
                }

                stage('Docker Build & Push Order Service') {
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

        stage('Payment Service') {
            when {
               changeset "**/payment/*.*"
               beforeAgent true
            }
            stages {
                stage('Build Payment Service') {
                    steps {
                        container('maven') {
                            dir('payment') {
                                sh 'mvn clean package'
                            }
                        }
                    }
                }

                stage('Docker Build & Push Payment Service') {
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

        stage('Inventory Service') {
            when {
               changeset "**/Inventory/*.*"
                beforeAgent true
            }
            stages {
                stage('Build Inventory Service') {
                    steps {
                        container('maven') {
                            dir('inventory') {
                                sh 'mvn clean package'
                            }
                        }
                    }
                }

                stage('Docker Build & Push Inventory Service') {
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
    }
}
        // Add more stages for other microservices following the same structure
