FROM openjdk:11.0.3-jre-slim

ENV VERSION=1.0.0
ENV PACKAGE_NAME="order-${VERSION}"

COPY target/${PACKAGE_NAME}.jar /opt/project/

ENV SERVER.PORT=8082

EXPOSE 8082

CMD ["bash", "-c", "java -jar /opt/project/${PACKAGE_NAME}.jar"]
