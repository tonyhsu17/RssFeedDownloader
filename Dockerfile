FROM docker.io/maven:eclipse-temurin AS builder
WORKDIR /app
COPY . .
RUN mvn clean compile assembly:single

FROM docker.io/eclipse-temurin:23.0.2_7-jre-ubi9-minimal
WORKDIR /app
COPY --from=builder /app/target/rss-feed-downloader-1.0-jar-with-dependencies.jar /app/rss-feed-downloader-1.0.jar

CMD ["java", "-jar", "rss-feed-downloader-1.0.jar"]
