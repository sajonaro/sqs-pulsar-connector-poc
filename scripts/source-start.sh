#!/usr/bin/env bash


echo 'create & start  sqs source connector'
docker compose exec -i pulsar-server  bin/pulsar-admin  sources create  \
  --archive /pulsar/connectors/sqs-source-connector-0.0.1.jar \
  --classname ep.SqsSourceConnector \
  --tenant public  \
  --namespace default  \
  --name sqs-source  \
  --destination-topic-name sqs-data-stream   \
  --source-config-file /pulsar/conf/source-config.yaml  \
  --parallelism 1

echo 'start connector '
docker compose exec pulsar-server  \
    bin/pulsar-admin  sources start \
            --name sqs-source  

echo 'check the status of connector '
docker compose exec pulsar-server  bin/pulsar-admin sources status --name sqs-source