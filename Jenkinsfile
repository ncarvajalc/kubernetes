pipeline {
    agent any
    
    environment {
        VERSION = "${env.BUILD_NUMBER}"
        // No registry for local testing
        DOCKER_REGISTRY = ''
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Configure Docker for Minikube') {
            steps {
                script {
                    // Connect to Minikube's Docker daemon
                    sh "eval \$(minikube docker-env)"
                }
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
        
        stage('Build Docker Images') {
            steps {
                script {
                    // Build API image
                    dir('products-api') {
                        sh "docker build -t products-api:${VERSION} ."
                    }
                    
                    // Build Frontend image
                    dir('products-front') {
                        sh "docker build -t products-front:${VERSION} ."
                    }
                }
            }
        }
        
        stage('Deploy to Minikube') {
            steps {
                script {
                    // Create namespace if it doesn't exist
                    sh "kubectl create namespace products --dry-run=client -o yaml | kubectl apply -f -"
                    
                    // Create or update the database secret
                    sh """
                    kubectl create secret generic db-credentials \
                      --from-literal=username=postgres \
                      --from-literal=password=postgres \
                      -n products --dry-run=client -o yaml | kubectl apply -f -
                    """
                    
                    // Apply Kubernetes manifests
                    sh "kubectl apply -f k8s/postgres-pvc.yaml -n products"
                    sh "kubectl apply -f k8s/postgres-deployment.yaml -n products"
                    sh "kubectl apply -f k8s/postgres-service.yaml -n products"
                    
                    // Update deployment files with correct image names and apply
                    sh """
                    cat k8s/products-api-deployment.yaml | \
                    sed 's|\${DOCKER_REGISTRY}/products-api:\${VERSION}|products-api:${VERSION}|g' | \
                    kubectl apply -f - -n products
                    """
                    
                    sh "kubectl apply -f k8s/products-api-service.yaml -n products"
                    
                    sh """
                    cat k8s/products-front-deployment.yaml | \
                    sed 's|\${DOCKER_REGISTRY}/products-front:\${VERSION}|products-front:${VERSION}|g' | \
                    kubectl apply -f - -n products
                    """
                    
                    sh "kubectl apply -f k8s/products-front-service.yaml -n products"
                    
                    // Apply ingress
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
        
        stage('Configure Local Access') {
            steps {
                script {
                    // Enable Minikube ingress addon if not already enabled
                    sh "minikube addons enable ingress || true"
                    
                    // Get Minikube IP
                    sh "echo 'Access your application at: http://\$(minikube ip)'"
                    
                    // Create a tunnel for the services (optional)
                    sh "echo 'Alternatively, run: minikube service products-front -n products --url'"
                }
            }
        }
    }
    
    post {
        success {
            echo 'Deployment completed successfully!'
        }
        failure {
            echo 'Deployment failed'
        }
    }
}