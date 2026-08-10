pipeline {
    agent any

    environment {
        // Credentials IDs (must match what you created)
        DOCKERHUB_CREDS = credentials('dockerhub-creds')
        GITHUB_CREDS   = credentials('github-creds')

        IMAGE = "akrembejaoui/migration"
        TAG   = "backend-${env.BUILD_NUMBER}"
        MANIFEST_REPO = "https://github.com/akrembejaoui/task-manager-manifests.git"
    }

    stages {
        stage('Build JAR') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Docker Build & Push') {
            steps {
                sh "docker build -t ${IMAGE}:${TAG} ."
                sh "echo $DOCKERHUB_CREDS_PSW | docker login -u $DOCKERHUB_CREDS_USR --password-stdin"
                sh "docker push ${IMAGE}:${TAG}"
            }
        }

        stage('Update Manifest Repository') {
            steps {
                sh """
                    # Clone manifest repo
                    git clone ${MANIFEST_REPO} manifests
                    cd manifests

                    # Update the image tag in the backend deployment file
                    # Adjust the file path if your manifests are structured differently
                    sed -i 's|image: ${IMAGE}:.*|image: ${IMAGE}:${TAG}|' backend/backend-deployment.yaml

                    # Commit and push using GitHub credentials
                    git config user.email "jenkins@local"
                    git config user.name "Jenkins CI"
                    git add backend/backend-deployment.yaml
                    git commit -m "Update backend image to ${TAG} [skip ci]"
                    git push https://${GITHUB_CREDS_USR}:${GITHUB_CREDS_PSW}@github.com/akrembejaoui/task-manager-manifests.git
                """
            }
        }
    }
}
