def call(String executor){
  script {
    if ( executor == "Mike" ){
      echo "Hi Mike"
    } else if (executor == "Jenkins" ){
      echo "Hi Jenkins"
    } else {
      echo "unrecognizable"
    }
  }
}
