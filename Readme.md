
# Goal of this PoC

This project contains a quick (and simple) example of how to create a source pulsar connector for AWS SQS service, in java.

Please refer: https://pulsar.apache.org/docs/3.1.x/io-develop/ for overview of the concept.

The below steps explain how to run the project ( locally).

## Quick overview
In docker-compose.yaml we configure pulsar broker (in standalone mode, for simplicity), and use Localstack to emulate SQS queue, and use amazon/aws-cli image to configure our queue. 



## Prerequisites
* mvn, java
* docker, docker compose
* chmod +x all scripts in ./scripts folder


### to build the sqs connector :
```
cd ./sqs-source-java && mvn clean package  -DskipTests
```

### to run pulsar cluster :
```
docker compose up -d
```

### to start the source connector :
```
./scripts/source-start.sh
```

### to see the results of connector's actions :
```
./scripts/consumer-start.sh
```

### (Optionally) to see the log output:
```
./scripts/get-logs.sh 
cat ./scripts/sqs-source-0.log
```

### TL/DR; version 
```
 ./scripts/build-and-run.sh 
```
