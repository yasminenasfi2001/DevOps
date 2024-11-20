FROM openjdk:11
EXPOSE 8089
WORKDIR /app
ADD http://127.0.0.1:8081/repository/maven-releases/tn/esprit/spring/gestion-station-ski/2.0/gestion-station-ski-2.0.jar gestion-station-ski-2.0.jar
ENTRYPOINT ["java", "-jar", "gestion-station-ski-2.0.jar"]
