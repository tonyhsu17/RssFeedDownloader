import groovy.json.JsonSlurper

repoName = "tonyhsu17"
containerName = 'rss-feed-downloader'
version = '1.0'

node('maven') {
    withCredentials([dockerCert(credentialsId: 'docker-cert', variable: 'DOCKER_CERT_PATH'),
                     usernamePassword(credentialsId: 'docker-login', passwordVariable: 'pass', usernameVariable: 'username')]) {
        sh "cp -r \'$DOCKER_CERT_PATH\' /root/.docker/"
        sh "echo $pass | docker login --username $username --password-stdin"
    }
    stage('dockerize') {
        checkout scm
        sh "docker build -t ${repoName}/${containerName}:${version} ."
        sh "docker run ${repoName}/${containerName}:${version}"
        sh "docker push ${repoName}/${containerName}:${version}"
        catchError(buildResult: 'SUCCESS', message: 'Skipped pushing to stable') {
            input 'Push to stable?'
            sh "docker tag ${repoName}/${containerName}:${version} ${repoName}/${containerName}:stable"
            sh "docker push ${repoName}/${containerName}:stable"
        }
    }
}
