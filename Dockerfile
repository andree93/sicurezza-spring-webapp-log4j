FROM maven:3.6-jdk-8-slim AS build
COPY . /usr/src/app/
WORKDIR /usr/src/app/
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM openjdk:8u181-jdk-alpine
WORKDIR /
COPY --from=build /usr/src/app/target/*.jar /app.jar
CMD java -jar app.jar