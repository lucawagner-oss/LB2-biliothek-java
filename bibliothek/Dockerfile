FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /build/target/bibliothek-1.0-SNAPSHOT.jar app.jar
COPY --from=build /build/target/lib ./lib
ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "ch.m450.bibliothek.Main"]