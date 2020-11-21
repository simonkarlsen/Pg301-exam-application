#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build . --tag exam1image --build-arg JAR_FILE=./target/Docker-0.0.1-SNAPSHOT.jar
docker tag exam1 simonkarlsen/exam1image
docker push exam1 simonkarlsen/exam1image