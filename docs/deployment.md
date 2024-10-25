## Deploy to Azure

This guide will show you how to deploy the Finance application to Azure using Azure Kubernetes Service (AKS).

### Prerequisites

- Azure account (free is enough)
- Docker installed

### Step 1: Create a Resource Group

A resource group is a container that holds related resources for an Azure solution.

1. Go to the Azure Portal and search for `Resource groups`.
2. Click on `+ Create`.
3. In the `Basics` tab:
   1. Name it `finance-rg`.
   2. Select the region closest to you.
4. Click on `Review + create` and then `Create`.

### Step 2: Create a Container Registry

A container registry is a private Docker registry in Azure that stores and manages container images.

1. Go to the Azure Portal and search for `Container registries`.
2. Click on `+ Create`.
3. In the `Basics` tab:
   1. Select the resource group `finance-rg`.
   2. Name it, e.g., `yournamefinanceacr`.
   3. Choose the `Basic` pricing plan.
4. Click on `Review + create` and then `Create`.

### Step 3: Prepare the Terminal

1. Open the terminal.
2. Set an environment variable for your Azure Container Registry:
   ```sh
   export AZURE_CONTAINER_REGISTRY=<insert-registry-name>.azurecr.io
   ```
3. Login into Azure:
   ```sh
   az login
   az acr login --name $AZURE_CONTAINER_REGISTRY
   ```
4. Select your subscription.
5. Set the resource group as default:
   ```sh
   az configure --defaults group=finance-rg
   ```

### Step 4: Build and Push Images to the Registry

With the registry created, you can now build and push the images to it.

1. Build the images:
    ```sh
    docker compose build
    ```
2. Tag the images:
    ```sh
    docker tag finance-backend $AZURE_CONTAINER_REGISTRY/backend
    docker tag finance-frontend $AZURE_CONTAINER_REGISTRY/frontend
    ```
3. Push the images:
    ```sh
    docker push $AZURE_CONTAINER_REGISTRY/backend
    docker push $AZURE_CONTAINER_REGISTRY/frontend
    ```

### Step 5: Create a Kubernetes Cluster

A Kubernetes cluster is a group of nodes that run containerized applications. In Azure, you can create a Kubernetes cluster with Azure Kubernetes Service (AKS).

1. Go to the Azure Portal and search for `Kubernetes services`.
2. Click on `+ Create` and choose `Kubernetes cluster`.
3. In the `Basics` tab:
   1. Select the resource group `finance-rg`.
   2. Name it `finance-aks`.
4. In the Integrations tab:
   1. Select your container registry.
5. Click on `Review + create` and then `Create`.

### Step 6: Configure the Kubernetes Cluster

1. Open the terminal.
2. Set the Kubernetes credentials on the local machine:
   ```sh
   az aks get-credentials --name finance-aks
   ```

### Step 7: Install the NGINX Ingress Controller

The NGINX Ingress Controller is an application that enables Kubernetes to manage external access to services within a cluster.

1. Install the NGINX Ingress Controller:
   ```sh
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
   ```
2. Check if the controller is running:
   ```sh
   kubectl get pods -n ingress-nginx
   ```

### Step 8: Install phpMyAdmin

1. Create a namespace to organize the resources:
   ```sh
   kubectl create namespace finance-phpmyadmin
   ```
2. Set an empty environment variable for the target MariaDB services:
   ```sh
   export MARIADB_SERVICES=
   ```
3. Create PHPMyAdmin Deployment, Service and Ingress:
   ```sh
   envsubst < k8s/phpmyadmin-deployment.yaml | kubectl apply -f -
   envsubst < k8s/phpmyadmin-service.yaml | kubectl apply -f -
   envsubst < k8s/phpmyadmin-ingress.yaml | kubectl apply -f -
   ```
4. Get the external IP of the ingress controller:
   ```sh
   kubectl get ingress -n finance-phpmyadmin
   ```
5. Add a new DNS record to the domain:
   - Type: A
   - Name: db
   - Value: <insert-external-ip>

### Step 9: Add new Finance Application Instance to the Cluster

Repeat this step for each new instance you want to add to the cluster.

1. Set the environment variable for your target environment:
   ```sh
   export ENVIRONMENT=<insert-environment>
   ```
2. Set the environment variable for your domain:
   ```sh
   export DOMAIN=<insert-domain>
   ```
3. Create a random environment file:
   ```sh
   echo "DATABASE_USER=$(awk 'BEGIN{srand(); printf "%08d\n", int(10000000 + rand() * 90000000)}')" > .env/$ENVIRONMENT.env
   echo "DATABASE_PASS=$(openssl rand -base64 16 | head -c 16)" >> env/$ENVIRONMENT.env
   ```
4. Create the namespace to organize the resources:
   ```sh
   kubectl create namespace finance-$ENVIRONMENT
   ```
5. Create a secret with the database credentials:
   ```sh
   kubectl create secret generic database-secret --from-env-file=env/$ENVIRONMENT.env --namespace=finance-$ENVIRONMENT
   ```
6. Create MariaDB Persistent Volume, Deployment, and Service:
   ```sh
   envsubst < k8s/mariadb-pvc.yaml | kubectl apply -f -
   envsubst < k8s/mariadb-deployment.yaml | kubectl apply -f -
   envsubst < k8s/mariadb-service.yaml | kubectl apply -f -
   ```
7. Create Backend Deployment and Service:
   ```sh
   envsubst < k8s/backend-deployment.yaml | kubectl apply -f -
   envsubst < k8s/backend-service.yaml | kubectl apply -f -
   ```
8. Create Frontend Deployment and Service:
   ```sh
   envsubst < k8s/frontend-deployment.yaml | kubectl apply -f -
   envsubst < k8s/frontend-service.yaml | kubectl apply -f -
   ```
9. Create the ingress resource:
   ```sh
   envsubst < k8s/instance-ingress.yaml | kubectl apply -f -
   ```
10. Get the external IP of the ingress controller:
    ```sh
    kubectl get ingress -n finance-$ENVIRONMENT
    ```
11. Add a new DNS record to the domain:
    - Type: A
    - Name: prod (or dev, test, etc.)
    - Value: <insert-external-ip>
12. Add the MariaDB service to the environment variable:
    ```sh
    export MARIADB_SERVICES=$MARIADB_SERVICES,mariadb-service.finance-$ENVIRONMENT.svc.cluster.local
    ```
13. Update the phpMyAdmin deployment:
    ```sh
    envsubst < k8s/phpmyadmin-deployment.yaml | kubectl apply -f -
    ```

---

### Troubleshooting

- Check if the pods are running:
  ```sh
  kubectl get pods -n <insert-environment>
  ```
- Check if the services are running:
  ```sh
  kubectl get svc -n <insert-environment>
  ```
- Check if the ingress is running:
  ```sh
  kubectl get ingress -n <insert-environment>
  ```
- Check the logs of a pod:
  ```sh
  kubectl logs <pod-name> -n <insert-environment>
  ```
