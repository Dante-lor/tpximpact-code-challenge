FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /build
COPY . .

RUN mvn clean package

FROM gcr.io/distroless/java21-debian12

WORKDIR /app
COPY --from=builder /build/target/shortener-service-*.jar app.jar

CMD [ "app.jar" ]