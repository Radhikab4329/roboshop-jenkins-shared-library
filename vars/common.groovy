def compile() {
  if (app_lang == "nodejs") {
    sh 'npm install'
  }

  if (app_lang == "maven") {
    sh 'mvn package'
  }

}

def unittests() {

  if (app_lang == "nodejs") {
     sh 'npm test || true'

  }

  if (app_lang == "maven") {
    sh 'mvn test'
  }

  if (app_lang == "python") {
    sh 'python3 -m unittest'
  }
}

def email(email_note) {
  mail bcc: '', body: "Job Failed - ${JOB_BASE_NAME}\nJenkins URL - ${JOB_URL}", cc: '', from: 'radhika.bhagavatham@gmail.com', replyTo: '', subject: 'TEST FROM JENKINS', to: 'radhika.bhagavatham@gmail.com'
}

def artifactPush() {
  sh "echo ${TAG_NAME} >VERSION"

  if (app_lang == "nodejs") {
    sh "zip -r ${component}-${TAG_NAME}.zip node_modules server.js VERSION ${extraFiles}"
  }

  if (app_lang == "nginx" || app_lang == "nginx") {
    sh "zip -v -r ${component}-${TAG_NAME}.zip * -x Jenkinsfile"
  }

  NEXUS_PASS = sh (script: 'aws ssm get-parameters --region us-east-1 --names nexus.pass --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
  NEXUS_USER = sh (script: 'aws ssm get-parameters --region us-east-1 --names nexus.user --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
  wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${NEXUS_PASS}", var: 'SECRET']]]) {
    sh "curl -v -u ${NEXUS_USER}:${NEXUS_PASS} --upload-file ${component}-${TAG_NAME}.zip http://3.238.8.60:8081/repository/${component}/${component}-${TAG_NAME}.zip"
  }

}