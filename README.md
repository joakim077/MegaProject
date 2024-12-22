# Run Bank app in EKS Cluster

### 1. EKS Cluster Configuration
skip for now

### 2. Run Database
Deploy DataBase servers
```bash    
    # kubectl apply -f mysql-configmap.yaml

    kubectl apply -f db-secret.yaml

    kubectl apply -f app-config.yaml

```
Install MySQL Operator for Kubernetes

- deploy the Custom Resource Definition (CRDs):
    ```bash
      kubectl apply -f https://raw.githubusercontent.com/mysql/mysql-operator/9.1.0-2.2.2/deploy/deploy-crds.yaml
      #8.0.33-2.0.9 
    ```
- deploy MySQL Operator for Kubernetes:
    ```bash
      kubectl apply -f https://raw.githubusercontent.com/mysql/mysql-operator/9.1.0-2.2.2/deploy/deploy-operator.yaml
    ```
- Verify the operator is running by checking the deployment inside the mysql-operator namespace:
  ```bash
    kubectl get deployment -n mysql-operator mysql-operator
  ```

- Deploy  MySQL InnoDB Cluster
  ```bash
      kubectl create secret generic mypwds \
        --from-literal=rootUser=root \
        --from-literal=rootHost=% \
        --from-literal=rootPassword="superSecret"
      
      kubectl apply -f mysql-innodb.yaml

      # it will create 2 service.
      # clusterIP and headless.
  ```


- check 
  ```bash
    kubectl run --rm -it myshell --image=container-registry.oracle.com/mysql/community-operator -- mysqlsh

    MySQL JS>  \connect root@mycluster
    Creating a session to 'root@mycluster'
    Please provide the password for 'root@mycluster': ******
    MySQL mycluster JS>
  ```
- Exec into mycluster-0 pod and create db
  ```bash
    kubectl exec -it mycluster-0 -- bash
    mysql -h localhost -uroot -pTest@123
    create db BankDB;
  ```
**Note** JDBC URL
{clustername}.svc.cluster.local

### 3. Deploy application

Deploy application
```bash
  kubectl apply -f app-deployment.yaml

  kubectl apply -f app-service.yaml

```

### 4. Cert Manager

Install Cert Manager into your Kubernetes cluster.
```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.5/cert-manager.yaml
```
Apply manifests
```bash
kubectl apply -f cluster_issuer.yaml
kubectl apply -f certificate
```

### 5 .Ingress
Install Nginx Ingress Controller
```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.9.4/deploy/static/provider/cloud/deploy.yaml
```

Apply Ingress Resource Manifest
```bash
kubectl apply -f ingress.yaml
```

### 6. Access application
Access application to URL: <Your_Domain>
```url
https://bank.joakim.online
```

### 7. HPA and Load Testing

Install Metrics Server
```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

Apply HPA 
```bash
kubectl apply -f hpa.yaml
```

Install Grafana K6 and generate load to application and you will see new pods are created.
```bash
k6s run load.js
```