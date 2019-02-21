FROM openjdk:8u151-jdk-alpine
MAINTAINER John Doe "john.doe@example.com"

EXPOSE 8080

WORKDIR /app
#COPY *.jar application.jar
#COPY src/backend/CavaMedia/build/libs/CavaMedia-0.1.jar application.jar
COPY src/backend/CavaMedia/build/libs/CavaMedia-0.1.jar application.jar

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/application.jar"]