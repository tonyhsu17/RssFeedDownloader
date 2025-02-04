import groovy.json.JsonSlurper

version = '1.0'

node('maven') {
    withCredentials([dockerCert(credentialsId: 'docker-cert', variable: 'DOCKER_CERT_PATH')]) {
        sh "cp -r \'$DOCKER_CERT_PATH\' /root/.docker/"
    }
    stage('rss downloader') {
        checkout scm
        sh "docker build -t org.tonyhsu17.rss-downloader:${version} ."
        withCredentials([string(credentialsId: 'rss-url', variable: 'RSS_URL')]) {
            sh "export RSS_DES=/shares/unsorted-downloads/watch; docker run org.tonyhsu17.rss-downloader:${version} "
//            sh script: """
//                mvn clean compile assembly:single
//                ls target
//                java -jar target/rss-feed-downloader-1.0-jar-with-dependencies.jar -d \"/shares/unsorted-downloads/watch\" -u \"${RSS_URL}\"
//                """
        }
    }
}
