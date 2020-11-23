FROM maven:3.6-jdk-11 as builder

# Copy the code to the container image
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Release artifact build
RUN mvn package -DskipTests

FROM adoptopenjdk/openjdk11:alpine-slim

# Copy jar to image
COPY --from=builder /app/target/exam-*.jar /exam-0.0.1-SNAPSHOT.jar

# Run on container startup
ENTRYPOINT ["java","-jar","/app/exam-0.0.1-SNAPSHOT.jar"]
