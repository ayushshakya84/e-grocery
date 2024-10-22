function stop() {
  helm uninstall $1
}

stop "rabbitmq"
stop "ingress"
stop "s3"
stop "keycloak"
stop "gateway"
stop "ordersaga"
stop "search"
stop "order"
stop "payment"
stop "profile"
stop "shipment"
stop "product"
stop "notification"

echo "___________"
echo "DONE!"
$SHELL
