def call() {

  if(!env.SONAR_EXTRA_OPTS) {
    env.SONAR_EXTRA_OPTS = " "
  }
  try {
    node('workstation') {

      stage('Checkout') {
        cleanWs()
        git branch: 'main', url: "https://github.com/Radhikab4329/${component}"
        sh 'env'

      }

      stage('compile/Build') {
        common.compile()
      }

      stage('Unit Tests') {
        common.unittests()
      }

      stage('Quality Control') {
        SONAR_PASS = sh (script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
        SONAR_USER = sh (script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
//          sh "sonar-scanner -Dsonar.host.url=http://34.234.225.46:9000 -Dsonar.login=${SONAR_USER} -Dsonar.password=${SONAR_PASS} -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true ${SONAR_EXTRA_OPTS}"
          sh "echo Sonar Scan"

        }
      }
      stage('Upload code to centralized place') {
        echo 'Upload'
      }



    }

  } catch(Exception e) {
    common.email("Failed")
  }
}

