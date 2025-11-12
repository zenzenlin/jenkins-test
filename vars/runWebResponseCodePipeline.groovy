def call(Map pipelineParams) {
  pipeline {
    agent any
    stages {
      stage ("Request website") {
        steps {
          script {
            response_code = sh (
              script: "curl -o /dev/null -s -w %{http_code} ${pipelineParams.domain}",
              returnStdout: true
            ).toInteger()

            echo "${response_code}"
          }
        }
      }
      stage ("Check response code") {
        steps {
          script {
            if ( response_code != pipelineParams.code ) {
              sh "false"
            }
          }
        }
      }
    }
    post {
      success {
        echo "success"
        withCredentials([string(credentialsId: 'email', variable: 'ADDRESS')]) {
          emailext (
            subject: "[🔥 FAILURE ALERT] ${env.JOB_NAME} - 網站連線錯誤！",
            body: "請檢查 Jenkins Build 紀錄 ${env.BUILD_URL} 以獲取詳細資訊。",
            to: ADDRESS
          )
          sh '''
              message="www.example.com response code === 200."
              ${message}
          '''
        }
      }
      failure {
        script {
          withCredentials([string(credentialsId: 'email', variable: 'ADDRESS')]) {
            emailext (
              subject: "[🔥 FAILURE ALERT] ${env.JOB_NAME} - 網站連線錯誤！",
              body: "請檢查 Jenkins Build 紀錄 ${env.BUILD_URL} 以獲取詳細資訊。",
              to: RECIPIENT
            )
            sh '''
                message="www.example.com response code != 200."
                curl -X GET https://api.telegram.org/bot${TOKEN}/sendMessage -d "chat_id=${GROUP_ID}&text=${message}"
            '''
          }
        }
      }
    }
  }
}
