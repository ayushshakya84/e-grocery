FROM openjdk:11.0.3-jre-slim

ENV VERSION=1.0.0
ENV PACKAGE_NAME="gateway-${VERSION}"

COPY target/${PACKAGE_NAME}.jar /opt/project/

ENV SERVER.PORT=80

EXPOSE 80

CMD ["bash", "-c", "java -jar /opt/project/${PACKAGE_NAME}.jar"]
