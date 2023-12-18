#!/usr/bin/env bash


docker compose down
cd ../sqs-source-java/&& mvn clean package
cd -
docker compose up -d
./source-start.sh
./consumer-start.sh