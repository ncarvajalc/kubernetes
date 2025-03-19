# Aplicación Full Stack de Gestión de Productos

Esta aplicación demuestra una solución Full Stack para gestionar productos utilizando Spring Boot en el backend, Next.js en el frontend, con un pipeline de CI/CD en Jenkins y despliegue automático en Kubernetes.

## Características principales

* CRUD completo de productos
* Backend REST API con Spring Boot y PostgreSQL
* Frontend con Next.js y React Query (Client side fetching)
* Diseño con TailwindCSS + ShadCN
* CI/CD automatizado con Jenkins
* Despliegue en Kubernetes

## Estructura del proyecto

```
.
├── products-api/              # Backend Spring Boot
├── products-front/            # Frontend Next.js
│
├── k8s/                       # Archivos de Kubernetes
│   ├── postgres-deployment.yaml
│   ├── postgres-pvc.yaml
│   ├── postgres-service.yaml
│   ├── products-api-deployment.yaml
│   ├── products-api-service.yaml
│   ├── products-front-deployment.yaml
│   ├── products-front-service.yaml
│   └── ingress.yaml
│
└── Jenkinsfile                # Pipeline CI/CD
```

## Requisitos previos

* Cluster de Kubernetes
* Jenkins con los siguientes plugins:
  * Docker Pipeline
  * Kubernetes CLI
  * Credentials Binding
* Registro de contenedores (GitHub Container Registry en este caso)
* Credenciales almacenadas en Jenkins:
  * `github-token`: Token de acceso a GitHub con permisos para publicar en el registro de contenedores
  * `db-credentials`: Credenciales para la base de datos PostgreSQL
  * Estas deben guardarse como credenciales de usuario con contraseña de Jenkins

## Configuración de CI/CD

El pipeline Jenkins automatiza todo el proceso desde el desarrollo hasta el despliegue:

1. **Checkout**: Descarga el código fuente del repositorio
2. **Build API**: Compila la aplicación Spring Boot
3. **Test API**: Ejecuta las pruebas del backend. Estas son pruebas unitarias y de integración
4. **Build Frontend**: Compila la aplicación Next.js
5. **Build and Push Docker Images**: Crea y publica las imágenes Docker al registro de contenedores de GitHub
6. **Deploy to Kubernetes**: Despliega la aplicación en Kubernetes
7. **Verify Deployment**: Verifica que los despliegues estén funcionando correctamente

### Ajustes necesarios para personalizar el despliegue

Para adaptar este proyecto a tu entorno específico, necesitarás realizar los siguientes ajustes:

1. **Repositorio y registro de contenedores**:
   * Modifica la variable `GITHUB_REGISTRY` en el Jenkinsfile para que apunte a tu repositorio de GitHub
   * Ejemplo: `GITHUB_REGISTRY = 'ghcr.io/tu-usuario'`

2. **Kubernetes**:
   * Asegúrate de que Jenkins tenga configurado el acceso al cluster de Kubernetes
   * Si estás usando GKE: `gcloud container clusters get-credentials tu-cluster --zone tu-zona --project tu-proyecto`
   * Si estás usando AKS: `az aks get-credentials --resource-group tu-grupo --name tu-cluster`
   * Si estás usando EKS: `aws eks update-kubeconfig --name tu-cluster --region tu-region`
   * La máquina que usa Jenkins necesita tener acceso a la red del cluster y tener instalado `kubectl`

3. **Dominio**:
   * Modifica el archivo `k8s/ingress.yaml` para usar tu dominio
   * Reemplaza `host: products.example.com` con tu dominio real
   * En caso de hacer uso de un dominio local como minikube, puedes añadir una entrada en el archivo `/etc/hosts` de tu máquina de la siguiente manera:

    ```shell
    echo "$(minikube ip) products.example.com" | sudo tee -a /etc/hosts
    ```

    * Note que se debe reemplazar `products.example.com` por el dominio que se desee utilizar y debe ser el mismo que se haya configurado en el archivo `k8s/ingress.yaml`

4. **Credenciales**:
   * Crea las siguientes credenciales en Jenkins:
     * `github-token`: Credencial tipo Username with Password para GitHub
     * `db-credentials`: Credencial tipo Username with Password para PostgreSQL
5. **Variables de entorno de NextJS**:
    * Modifica el archivo `products-front/Dockerfile` para incluir la variable de entorno `NEXT_PUBLIC_API_URL` con la URL de tu API. Esta debe ser el mismo dominio que configuraste en el Ingress de Kubernetes debido a que el frontend se comunica con react query, por tanto la comunicación es del lado del cliente.

    ```Dockerfile
    ENV NEXT_PUBLIC_API_URL=http://products.example.com/api
    ```

## Despliegue en Kubernetes

### Componentes desplegados

1. **Base de datos PostgreSQL**:
   * Credenciales almacenadas como secretos de Kubernetes

2. **Backend (products-api)**:
   * Expuesto internamente como servicio

3. **Frontend (products-front)**:
   * Configurado para comunicarse con el backend

4. **Ingress**:
   * Enrutamiento basado en paths para frontend y backend
   * Exposición del servicio al exterior

### Variables de entorno

El frontend utiliza la variable `NEXT_PUBLIC_API_URL` para comunicarse con el backend. Esta variable debe estar correctamente configurada durante la compilación del frontend, ya que Next.js incorpora estas variables en el momento de la compilación.

## Ejecución local

Para ejecutar el proyecto localmente:

1. **Backend**:

   ```bash
   cd products-api
   ./mvnw spring-boot:run
   ```

2. **Frontend**:

   ```bash
   cd products-front
   pnpm install
   pnpm run dev
   ```

## Mejoras futuras

* Debido al alcance de la prueba, no se implementaron pruebas a nivel de frontend. Se recomienda implementar pruebas de integración y E2E para garantizar la calidad del código. Estas se pueden agregar de manera modular en el pipeline de Jenkins.
* Implementar un sistema de autenticación y autorización para proteger las rutas de la API y el frontend. Actualmente, la API sería accesible públicamente y no habría control de acceso.
* Mejorar la configuración de Kubernetes para un entorno de producción, como configurar un Ingress Controller con certificados SSL y configurar un sistema de monitoreo y logging. Se podría hacer uso de herramientas como Prometheus y Grafana para monitoreo y EFK (Elasticsearch, Fluentd, Kibana) para logging. Esto dependerá de las necesidades específicas de la aplicación. No fue requerido para esta prueba debido a su alcance.
* Implementar una estrategia de rollback en el pipeline de Jenkins en caso de fallos en el despliegue. Esto puede incluir además notificaciones por correo electrónico o Slack en caso de fallos.
* Se decidió usar react-query a nivel de cliente debido a su facilidad de uso y configuración. Sin embargo, se podría considerar usarlo a nivel de servidor para sacar el máximo provecho a Next.js y su renderizado híbrido. Esto dependerá de las necesidades de la aplicación y de la complejidad de los datos a manejar.
