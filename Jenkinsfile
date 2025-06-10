pipeline {
  agent any

  environment {
    // these should match the env var names you use locally/Jenkins
    DB_URL         = credentials('DB_URL')
    DB_USERNAME    = credentials('DB_USERNAME')
    DB_PASSWORD    = credentials('DB_PASSWORD')
  }

  stages {
    stage('Checkout') {
      steps {
        // pulls from the configured SCM
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh './mvnw clean install -DskipTests=false'
      }
    }

    stage('Run App (Smoke Test)') {
      steps {
        sh 'nohup ./mvnw spring-boot:run &'
        sh 'sleep 15'              // give the app time to start
        sh 'curl --fail http://localhost:8080/actuator/health'
      }
    }

    stage('Cleanup') {
      steps {
        // kill any Java process listening on 8080
        sh "fuser -k 8080/tcp || true"
      }
    }
  }

  post {
    always {
      junit '**/target/surefire-reports/*.xml'
    }
  }
}
