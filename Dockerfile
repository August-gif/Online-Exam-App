FROM openjdk:17-jdk-slim

WORKDIR /app

# Copie le fichier JAR produit par Maven
COPY target/Online-exam-App_server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9192

ENTRYPOINT ["java", "-jar", "app.jar"]