#!/usr/bin/env sh

#check whats in the output (without consuming the messages)
docker compose exec  pulsar-server \
  bin/pulsar-client consume   public/default/sqs-data-stream   \
    --subscription-type Exclusive \
    --subscription-name $(uuidgen) \
    --num-messages 0  \
    --subscription-position Earliest



