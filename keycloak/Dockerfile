FROM jboss/keycloak:12.0.3

COPY E-GROCERY-users-0.json /opt/jboss/keycloak/E-GROCERY-users-0.json

ENV KEYCLOAK_USER admin
ENV KEYCLOAK_PASSWORD admin

ENV KEYCLOAK_IMPORT /opt/jboss/keycloak/E-GROCERY-users-0.json

EXPOSE 8080
