function install() {
  echo "Installing " $1
  cd $1
  helm dep up
  helm install $1 .
  cd ..
  echo "___________"
}

function installGateway() {
  echo "Installing gateway"
  cd gateway
  nodeIp=$(minikube ip)
  keycloakUrl=$(printf "http://%s:30080/auth" "$nodeIp")
  helm install "gateway" . --set keycloak.auth.server.url=$keycloakUrl
  cd ..
  echo "___________"
}

function installProfile() {
  echo "Installing profile"
  cd profile
  nodeIp=$(minikube ip)
  keycloakUrl=$(printf "http://%s:30080" "$nodeIp")
  helm install "profile" . --set keycloak.server.url=$keycloakUrl
  cd ..
  echo "___________"
}

function installSearch() {
  echo "Installing search"
  cd search
  helm dep up
  helm install "search" . --set elasticsearch.coordinating.replicas=1 --set elasticsearch.master.replicas=1 --set elasticsearch.data.replicas=1 --set elasticsearch.ingest.replicas=1
  cd ..
  echo "___________"
}

helm install rabbitmq bitnami/rabbitmq --set auth.username=guest,auth.password=guest
echo "___________"
install "keycloak"
sleep 10s

installGateway
installProfile
install "ingress"
install "s3"
installSearch
install "ordersaga"
install "order"
install "payment"
install "shipment"
install "notification"
install "product"

echo "___________"
echo "DONE!"
$SHELL