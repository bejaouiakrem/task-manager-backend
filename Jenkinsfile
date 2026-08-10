pipeline {
    agent any
    environment {
        DOCKERHUB_CREDS = credentials('dockerhub-creds')
        GITHUB_CREDS = credentials('github-creds')
        IMAGE = "akrembejaoui/migration"
        TAG = "backend-${env.BUILD_NUMBER}"
    }
    stages {
        stage('Build') {
            steps {
                sh 'chmod +x mvnw && ./mvnw clean package -DskipTests'
            }
        }
        stage('Docker Build & Push') {
            steps {
                sh "docker build -t ${IMAGE}:${TAG} ."
                sh "echo $DOCKERHUB_CREDS_PSW | docker login -u $DOCKERHUB_CREDS_USR --password-stdin"
                sh "docker push ${IMAGE}:${TAG}"
            }
        }
        stage('Update Manifest Repo') {
            steps {
                sh """
                    git clone https://github.com/bejaouiakrem/task-manager-manifests.git manifests
                    cd manifests
                    sed -i 's|image: .*|image: ${IMAGE}:${TAG}|' backend/backend-deployment.yaml
                    git config user.email "jenkins@local"
                    git config user.name "Jenkins"
                    git commit -am "update backend image to ${TAG} [skip ci]"
                    # Push with authentication using the token
                    git push https://${GITHUB_CREDS_USR}:${GITHUB_CREDS_PSW}@github.com/bejaouiakrem/task-manager-manifests.git
                """
            }
        }
    }
}
