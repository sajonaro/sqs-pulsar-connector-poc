package ep;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.pulsar.functions.api.Record;
import lombok.Data;
import org.slf4j.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message ;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import org.apache.pulsar.io.core.PushSource;

public class SqSListener implements Runnable{
   
    private Logger logger;
    private SqsClient sqsClient;
    private PushSource<String> source;
    private String queueUrl;

    public SqSListener(Logger logger, SqsClient sqsClient, String queueUrl, PushSource<String> source){
        this.logger=logger;
        this.sqsClient =sqsClient;
        this.queueUrl = queueUrl;
        this.source = source;
    }

    @Override
    public void run() {
     try{
            logger.info("Listening to SQS queue: {} ", queueUrl);
            
            // Receive messages from the queue with long polling (20 seconds)
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            if (!messages.isEmpty()) {
                for (Message message : messages) {
                  
                    String messageText = message.body();
                    logger.info("message read from SQS is: {} ", messageText);
                    
                    source.consume(new SqsRecord(messageText));

                    // Delete the message from the queue to avoid processing it again
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build();

                    sqsClient.deleteMessage(deleteRequest);


                }

        }} catch (Exception e) {
            logger.error("Filed while reading from sqs, details: ", e);
            e.printStackTrace();
        }
    }


    @Data
    private static class SqsRecord implements org.apache.pulsar.functions.api.Record<String> {
        private final String value;
    }
}