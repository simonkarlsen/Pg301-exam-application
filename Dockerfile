FROM maven:3.6-jdk-11 as builder

# Copy the code to the container image
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Release artifact build
RUN mvn package -DskipTests

FROM openjdk:11-jre

# Copy jar to prod image
COPY --from=builder /app/target/exam-*.jar /exam-0.0.1-SNAPSHOT.jar

# Run on container startup
CMD ["java", "-Djava.security.egdls =file:/dev/./urandom", "-jar", "/exam-0.0.1-SNAPSHOT.jar"]

