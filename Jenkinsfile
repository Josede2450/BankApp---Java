pipeline {
  agent any

environment {
    DB_URL = credentials('DB_URL')
    DB_USERNAME = credentials('DB_USERNAME')
    DB_PASSWORD = credentials('DB_PASSWORD')
    GOOGLE_CLIENT_ID = credentials('GOOGLE_CLIENT_ID')
    GOOGLE_CLIENT_SECRET = credentials('GOOGLE_CLIENT_SECRET')
    EXPERIAN_CLIENT_ID = credentials('EXPERIAN_CLIENT_ID')
    EXPERIAN_CLIENT_SECRET = credentials('EXPERIAN_CLIENT_SECRET')
    MAIL_USERNAME = credentials('MAIL_USERNAME')
    MAIL_PASSWORD = credentials('MAIL_PASSWORD')
}

  stages {
    stage('Checkout') {
      steps {
        // Clone the repo from SCM
        checkout scm
      }
    }


    stage('Build & Test') {
      steps {
        withEnv([
          "DB_URL=${env.DB_URL}",
          "DB_USERNAME=${env.DB_USERNAME}",
          "DB_PASSWORD=${env.DB_PASSWORD}"
        ]) {
          bat '.\\mvnw.cmd clean install -DskipTests=false'
        }
      }
    }


    stage('Cleanup') {
      steps {
        // Kill process on port 8080
        bat '''
        for /f "tokens=5" %%a in ('netstat -aon ^| find ":8080" ^| find "LISTENING"') do taskkill /F /PID %%a
        '''
      }
    }
  }

  post {
    always {
      junit '**/target/surefire-reports/*.xml'
    }
  }
}
