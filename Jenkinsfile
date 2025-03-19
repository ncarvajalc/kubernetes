pipeline {
    agent any
    
    environment {
        GITHUB_REGISTRY = 'ghcr.io/ncarvajalc' 
        VERSION = "${env.BUILD_NUMBER}"
        GITHUB_TOKEN = credentials('github-token')
        DB = credentials('db-credentials')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build API') {
            steps {
                dir('products-api') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }
        
        stage('Test API') {
            steps {
                dir('products-api') {
                    sh './mvnw test'
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                dir('products-front') {
                    sh 'pnpm install'
                    sh 'pnpm run build'
                }
            }
        }
        
        stage('Build and Push Docker Images') {
            steps {
                script {
                    // Login to GitHub Container Registry
                    sh "echo ${GITHUB_TOKEN_PSW} | docker login ghcr.io -u ${GITHUB_TOKEN_USR} --password-stdin"
                    
                    // Build and push API image
                    dir('products-api') {
                        sh "docker build -t ${GITHUB_REGISTRY}/products-api:${VERSION} ."
                        sh "docker push ${GITHUB_REGISTRY}/products-api:${VERSION}"
                    }
                    
                    // Build and push Frontend image
                    dir('products-front') {
                        sh "docker build -t ${GITHUB_REGISTRY}/products-front:${VERSION} ."
                        sh "docker push ${GITHUB_REGISTRY}/products-front:${VERSION}"
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Create namespace if it doesn't exist
                    sh "kubectl create namespace products --dry-run=client -o yaml | kubectl apply -f -"
                    
                    // Create secret for GitHub Container Registry
                    sh """
                    kubectl create secret docker-registry github-registry \
                      --docker-server=ghcr.io \
                      --docker-username=${GITHUB_TOKEN_USR} \
                      --docker-password=${GITHUB_TOKEN_PSW} \
                      --namespace=products \
                      --dry-run=client -o yaml | kubectl apply -f -
                    """

                    // Create secret for database credentials
                    sh """
                    kubectl create secret generic db-credentials \
                      --from-literal=username=${DB_USR} \
                      --from-literal=password=${DB_PSW} \
                      --namespace=products \
                      --dry-run=client -o yaml | kubectl apply -f -
                    """
                    
                    
                    // Apply Kubernetes manifests
                    sh "kubectl apply -f k8s/postgres-pvc.yaml -n products"
                    sh "kubectl apply -f k8s/postgres-deployment.yaml -n products" 
                    sh "kubectl apply -f k8s/postgres-service.yaml -n products"
                    
                    // Apply API and Frontend manifests with variable substitution
                    sh """
                    cat k8s/products-api-deployment.yaml | \
                    sed 's|\${DOCKER_REGISTRY}|${GITHUB_REGISTRY}|g; s|\${VERSION}|${VERSION}|g' | \
                    kubectl apply -f - -n products
                    """
                    
                    sh "kubectl apply -f k8s/products-api-service.yaml -n products"
                    
                    sh """
                    cat k8s/products-front-deployment.yaml | \
                    sed 's|\${DOCKER_REGISTRY}|${GITHUB_REGISTRY}|g; s|\${VERSION}|${VERSION}|g' | \
                    kubectl apply -f - -n products
                    """
                    
                    sh "kubectl apply -f k8s/products-front-service.yaml -n products"
                    sh "kubectl apply -f k8s/ingress.yaml -n products"
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                script {
                    // Wait for deployments to be ready
                    sh "kubectl rollout status deployment/products-api -n products --timeout=300s"
                    sh "kubectl rollout status deployment/products-front -n products --timeout=300s"
                }
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
            cleanWs()
        }
        success {
            echo 'Deployment completed successfully!'
        }
        failure {
            echo 'Deployment failed'
        }
    }
}