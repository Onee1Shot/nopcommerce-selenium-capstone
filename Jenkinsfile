pipeline {
  agent any

  tools {
    jdk 'JDK17'
    maven 'M3'
  }

  // Auto-build: poll the repo every ~2 minutes
  triggers {
    pollSCM('H/2 * * * *')
  }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        bat 'mvn -B -U clean test -Dbrowser=chrome -Dheadless=true'
      }
    }

    stage('Publish Reports') {
      steps {
        // JUnit XML
        junit 'target/surefire-reports/*.xml'

        // Cucumber HTML (requires "HTML Publisher" plugin)
        publishHTML(target: [
          reportDir: 'target',
          reportFiles: 'cucumber-report.html',
          reportName: 'Cucumber HTML',
          keepAll: true,
          alwaysLinkToLastBuild: true,
          allowMissing: true
        ])

        // Keep artifacts (HTML/JSON, screenshots)
        archiveArtifacts allowEmptyArchive: true,
          artifacts: 'target/**/*.json, target/**/*.html, target/**/screenshots/**/*'
      }
    }
  }

  post {
    always {
      cleanWs deleteDirs: false, notFailBuild: true
    }
  }
}
