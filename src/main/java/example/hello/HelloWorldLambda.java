package example.hello;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.micronaut.context.annotation.Requires;
import io.micronaut.function.aws.MicronautRequestHandler;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.regions.Region;

import jakarta.inject.Inject;
import java.util.Map;

@Requires(property = "aws.lambda.enabled", value = "true")
public class HelloWorldLambda extends MicronautRequestHandler<Map<String, String>, String> {

    private SqsClient sqsClient;

    private final String QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/851725606805/hello-name";  // Replace with actual queue URL

    public HelloWorldLambda() {
        // Manually initialize SQS client in case injection fails
        this.sqsClient = SqsClient.builder()
                .region(Region.US_EAST_2)  // Set your AWS region
                .build();
    }

    @Override
    public String execute(Map<String, String> input) {
        String user = input.get("user");

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        String message = "Hello " + user;

        if (sqsClient == null) {
            throw new IllegalStateException("SqsClient is not initialized");
        }

        // Send message to SQS
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody("Processed user: " + user)
                .build());

        return message;
    }
}