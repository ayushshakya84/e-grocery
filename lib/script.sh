#/bin/bash
echo "Installing dependency auth"
cd auth/
mvn clean install
mvn install:install-file -Dfile=target/auth-1.0.0.jar -DgroupId=ru.voleshko.lib -DartifactId=auth -Dversion=1.0.0 -Dpackaging=jar
echo "auth dependecy install succesfull"

echo "Installing dependency idempotency"
cd ../idempotency/
mvn clean install
mvn install:install-file -Dfile=target/idempotency-1.0.0.jar -DgroupId=ru.voleshko.lib -DartifactId=idempotency -Dversion=1.0.0 -Dpackaging=jar
echo "idempotency dependecy install succesfull"

echo "Installing dependency transactionaloutbox"
cd ../transactionaloutbox/
mvn clean install
mvn install:install-file -Dfile=target/transactionaloutbox-1.0.0.jar -DgroupId=ru.voleshko.lib -DartifactId=transactionaloutbox -Dversion=1.0.0 -Dpackaging=jar
echo "transactionaloutbox dependecy install succesfull"