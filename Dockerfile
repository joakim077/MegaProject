FROM maven:3.8.3-openjdk-17 AS builder

MAINTAINER joakim 

WORKDIR /src

COPY . /src

RUN mvn clean install -DskipTests=true


FROM openjdk:17-alpine as deployer

WORKDIR /src

COPY --from=builder /src/target/*.jar /src/target/bankapp.jar

EXPOSE 8080

ENTRYPOINT ["jave", "-jar", "/src/target/bankapp.jar"]