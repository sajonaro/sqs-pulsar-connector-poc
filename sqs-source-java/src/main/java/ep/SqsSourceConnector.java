package ep;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message ;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.pulsar.io.core.PushSource;
import org.apache.pulsar.io.core.SourceContext;
import org.slf4j.Logger;



public class SqsSourceConnector extends PushSource<String> {
    private static Logger logger;
    private static SqsClient sqsClient;
    private static ScheduledExecutorService scheduledThreadPool;

    @Override
    public void open(Map<String, Object> config, SourceContext sourceContext) throws Exception {
        try{

            logger = sourceContext.getLogger();
            logger.info("Starting AWS SQS Source connector...");

            String queueUrl = (String)config.get("queue_url");
            String secret = (String)config.get("accessSecret");
            String key = (String)config.get("accessKey");
            String region = (String)config.get("region");
            String localstackURL = (String)config.get("LOCALSTACK_URL");

            logger.info("queue url: {}",queueUrl);
            logger.info("localstack URL is: {}", localstackURL); 
            
            scheduledThreadPool = Executors.newScheduledThreadPool(1);
            sqsClient = initSqsClient(key, secret, region,localstackURL);
            scheduledThreadPool.scheduleAtFixedRate(new SqSListener(logger, sqsClient, queueUrl, this),0,20,TimeUnit.SECONDS);

        }catch(Exception exc){
            logger.error("connector failed, error is:"+exc.toString());
            exc.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        sqsClient.close();
        scheduledThreadPool.shutdown();
    }

    private SqsClient initSqsClient(String accessKey, String accessSecret, String awsRegion, String localStackURL) {
        SqsClientBuilder builder = SqsClient.builder();

        if(localStackURL != "")
            builder.endpointOverride(URI.create(localStackURL));

        return builder.region(Region.of(awsRegion))
        .credentialsProvider(StaticCredentialsProvider.create(new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return accessKey;
            }

            @Override
            public String secretAccessKey() {
                return accessSecret;
            }
         }))
        .build();
    }


}