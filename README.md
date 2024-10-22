# E-GROCERY SHOP

## About
A demo-project of grocery e-commerce application based on microservice architecture.

## Technologies
![Technologies used](docs/img/technologies.png?raw=true "Technologies used")

## Architecture
![Architecture](docs/img/architecture.png?raw=true "Architecture")

## Patterns used
- API gateway
- Message bus
- Saga
- Idempotent receiver
- Transactional outbox

## User interface example 
UI is based on bootstrap 4 [OGANI template](https://technext.github.io/ogani/#)
![UI](docs/img/user_interface.png?raw=true "Architecture")

## Getting Started
### Pre-requisites
- Minikube v1.17.1+
- Helm v3.1.0+

### Run on minikube
```
helm repo add bitnami https://charts.bitnami.com/bitnami
minikube addons enable ingress
cd deploy
sh run-minikube.sh

#Add to etc file:
<minikube_ip> e-grocery
```

- Visit URL: http://e-grocery/product-management

Access pre-configured `admin` account:
```
Login: admin
Pass: 123
```
- For accessing `user` role - register from scratch via keycloak form

- Keycloak would be available on <minikube_ip>:30080/auth

- To shut down chart installations, run ```sh stop-minikube.sh```

#### Enable monitoring
You could enable monitoring with Prometheus and Grafana dashboard for `Search service`:
```
cd deploy/search
helm install prom stable/prometheus-operator -f prometheus.yaml
kubectl port-forward service/prom-grafana 9000:80
# import grafana dashboard `product-catalogue-rps-dashboard.json` - shows RPS for searching product catalogue requests
```

## Features implemented
- Managing products with Administrator role
- Searching products by categories or with full-text search
- Shopping cart, checkout
- Mocked processes of payment, booking a shipment slot and products reservation
- Changing users profile data
- Notifications on order status change


