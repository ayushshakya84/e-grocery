FROM openjdk:11.0.3-jre-slim

ENV VERSION=1.0.0
ENV PACKAGE_NAME="shipment-${VERSION}"

COPY target/${PACKAGE_NAME}.jar /opt/project/

ENV SERVER.PORT=8085

EXPOSE 8085

CMD ["bash", "-c", "java -jar /opt/project/${PACKAGE_NAME}.jar"]
