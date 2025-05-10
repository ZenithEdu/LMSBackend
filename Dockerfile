#Build stage

FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /LMSBackend
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage

FROM openjdk:21-jdk
WORKDIR /LMSBackend

COPY --from=build /LMSBackend/target/LMSBackend-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar" ,"app.jar"]

