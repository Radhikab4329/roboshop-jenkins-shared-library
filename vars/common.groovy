def compile() {
  if (app_lang == "nodejs") {
    sh 'npm install'
    sh 'env'
  }

  if (app_lang == "maven") {
    sh 'mvn package'
  }

}

def unittests() {

  if (app_lang == "nodejs") {
     sh 'npm test'

  }

  if (app_lang == "maven") {
    sh 'mvn test'
  }

  if (app_lang == "python") {
    sh 'python3 -m unittest'
  }
}

def email(email_note) {
  mail bcc: '', body: 'Job Failed : ', cc: '', from: 'radhika.b4329@gmail.com', replyTo: '', subject: 'TEST FROM JENKINS', to: 'radhika.b4329@gmail.com'
}