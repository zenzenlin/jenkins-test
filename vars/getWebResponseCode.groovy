def call(String domain, Integer code) {
  stage("check website code ${domain}") {
    script {
      response_code = sh (
        script: "curl -o /dev/null -s -w %{http_code} ${domain}",
        returnStdout: true
      ).toInteger()

      echo "${response_code}"
    }
  }
  stage ("Check response code") {
    script{
      if ( response_code != code ){
        sh "false"
      }
    }
  }
}
