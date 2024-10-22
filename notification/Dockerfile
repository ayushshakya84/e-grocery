FROM openjdk:11.0.3-jre-slim

ENV VERSION=1.0.0
ENV PACKAGE_NAME="notification-${VERSION}"

COPY target/${PACKAGE_NAME}.jar /opt/project/

ENV SERVER.PORT=8088

EXPOSE 8088

CMD ["bash", "-c", "java -jar /opt/project/${PACKAGE_NAME}.jar"]
