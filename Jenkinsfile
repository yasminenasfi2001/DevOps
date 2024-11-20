pipeline {
    agent any

 environment {
     DOCKER_IMAGE_NAME_APP = 'yasminenasfi840/gestion-station-ski' 
     DOCKER_IMAGE_NAME_DB = 'yasminenasfi840/gestion-station-ski-db'
}

    stages {
        stage('Git') {
            steps {
                echo 'Pulling '
                git branch: 'Yasmine', credentialsId: '8614a299-a218-4f1d-b3ef-85c780a30d7d', url: 'https://github.com/yasminenasfi2001/DevOps_Station_Ski.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean'
                sh 'mvn compile'
            }
        }
          stage('Test JUnit-mockito') {
            steps {
                 sh 'mvn test -X'
                 sh 'mvn jacoco:report'

            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeYasminee') {
                    sh 'mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=*/target/jacoco-report/jacoco.xml'
                }
            }
         }

        stage('Deploy to Nexus') {
            steps {
              sh 'mvn package'
              sh 'mvn deploy' }
}
          
        stage('Docker Compose') {
            steps {
                script {
                  
                    sh 'docker compose -f docker-compose.yml up -d'
                }
            }
        }
 stage('Login, Tag and Push Docker Images') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-token', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_TOKEN')]) {
                       
                        sh "echo $DOCKER_TOKEN | docker login -u $DOCKER_USERNAME --password-stdin"

                        sh "docker tag yasmine-stationski-app:latest ${DOCKER_IMAGE_NAME_APP}:latest"
                        sh "docker push ${DOCKER_IMAGE_NAME_APP}:latest"
                  
                        sh "docker tag mysql:8.0 ${DOCKER_IMAGE_NAME_DB}:latest"
                        sh "docker push ${DOCKER_IMAGE_NAME_DB}:latest"
                    }
                }
            }
        }
}
   post {
        success {
            mail to: 'yasminenasfi2001@gmail.com',
                 subject: "Pipeline Successful: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Bonjour,\n\nLe pipeline '${env.JOB_NAME}' a réussi au build #${env.BUILD_NUMBER}.\n\nCordialement,\nL'équipe Jenkins"
        }
        failure {
            mail to: 'yasminenasfi2001@gmail.com',
                 subject: "Pipeline Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Bonjour,\n\nLe pipeline '${env.JOB_NAME}' a échoué au build #${env.BUILD_NUMBER}. Veuillez vérifier les logs pour plus de détails.\n\nCordialement,\nL'équipe Jenkins"
        }
    }       
}