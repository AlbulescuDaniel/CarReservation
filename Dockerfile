# BUILD STAGE
FROM maven:3.8-openjdk-17-slim AS build

RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests

# RUN STAGE
FROM openjdk:17-slim

RUN mkdir /app
COPY --from=build /project/target/*.jar /app/carreservation.jar
WORKDIR /app
CMD ["java", "-jar", "carreservation.jar"]
