package com.sk.fresh.aws.receiver;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.sk.fresh.SetupTestConfiguration;
import com.sk.fresh.config.AWSProperties;
import com.sk.fresh.repository.FreshDeadLetterMessageRepository;
import com.sk.fresh.repository.FreshDetailProcessingRepository;
import com.sk.fresh.repository.FreshMessageType;
import com.sk.fresh.repository.FreshReceivedMessageRepository;
import com.sk.fresh.repository.MessageStatus;
import io.awspring.cloud.messaging.listener.Acknowledgment;
import io.awspring.cloud.messaging.listener.QueueMessageAcknowledgment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
@Import(SetupTestConfiguration.class)
@Tag("IntegrationTest")
@Slf4j
class AWSQueueMessageListenerITTest {

     @Autowired
     private FreshReceivedMessageRepository freshReceivedMessageRepository;

     @Autowired
     private FreshDeadLetterMessageRepository freshDeadLetterMessageRepository;

     @Autowired
     private FreshDetailProcessingRepository freshDetailProcessingRepository;

     @Autowired
     private AmazonSQSAsync amazonSQSAsync;

     @Autowired
     private AWSQueueMessageListener awsQueueMessageListener;

     @Autowired
     private AWSProperties awsProperties;

     private List<AWSQueueMessageHandler> freshdeskQueueMessageHandlers = new ArrayList<>();

     @Autowired
     @Qualifier("objectMapper")
     private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

     @Container
     public static MariaDBContainer container = new MariaDBContainer()
             .withUsername("fresh")
             .withPassword("fresh")
             .withDatabaseName("freshdesk");

     @Container
     public static MockServerContainer mockContainer = new MockServerContainer("5.10.0");

     @DynamicPropertySource
     static void properties(DynamicPropertyRegistry registry) {
          registry.add("spring.datasource.url", container::getJdbcUrl);
          registry.add("spring.datasource.username", container::getUsername);
          registry.add("spring.datasource.password", container::getPassword);
          registry.add("spring.flyway.user", container::getUsername);
          registry.add("spring.flyway.password", container::getPassword);
          registry.add("spring.flyway.schemas", () -> "freshdesk");
          registry.add("spring.flyway.url", container::getJdbcUrl);

          //   Setup the Freshdesk Server to point to the MockServer endpoint
          registry.add("freshdesk.url", mockContainer::getEndpoint);
     }

     @BeforeEach
     void setUp() {
          awsQueueMessageListener = spy(new AWSQueueMessageListener(awsProperties,
                  objectMapper,
                  freshdeskQueueMessageHandlers,
                  freshReceivedMessageRepository,
                  freshDetailProcessingRepository,
                  freshDeadLetterMessageRepository));
     }

     /**
      * Test that the message listener received the message, was able to unpack it and inserted the root entity.
      * The processors are mocked for this test, so no actual processing happend.
      */
     @Test
     @DisplayName("Test the sqsListener is receiving the message")
     void testMessageListenerOnMessageReceived_rootEntityInserted() {

          var origAwsMessageId = "d2d711c5-bf93-49a8-af1f-bbcc94b05658";
          var senderId = "TestSender";
          var payload = buildQueueMessagePayload();

          Map<String, Object> allHeaders = new HashMap<>();
          allHeaders.put("MessageId", origAwsMessageId);
          allHeaders.put("SenderId", senderId);

          var ack = new QueueMessageAcknowledgment(amazonSQSAsync, null, null);

          ArgumentCaptor<String> masterPayloadCapture = ArgumentCaptor.forClass(String.class);
          ArgumentCaptor<Map<String, Object>> headerCapture = ArgumentCaptor.forClass(Map.class);
          ArgumentCaptor<Acknowledgment> acknowledgmentArgumentCaptor = ArgumentCaptor.forClass(Acknowledgment.class);

          awsQueueMessageListener.onMessage(payload, allHeaders, ack);

          var rootEntityOpt = freshReceivedMessageRepository.findByAwsQueueMessageId(origAwsMessageId);

          var freshReceivedMessageEntity = rootEntityOpt.get();
          assertEquals(1, freshReceivedMessageEntity.getFreshMessageId());
          assertEquals(origAwsMessageId, freshReceivedMessageEntity.getAwsQueueMessageId());
          assertEquals(FreshMessageType.CUSTOMER_CREATION, freshReceivedMessageEntity.getMessageType());
          assertEquals(0, freshReceivedMessageEntity.getRetryCount());
          assertEquals(10, freshReceivedMessageEntity.getLastDelayTime());
          assertNotNull(freshReceivedMessageEntity.getDateCreated());
          assertNotNull(freshReceivedMessageEntity.getDateModified());
          assertEquals(MessageStatus.PENDING_PROCESSING, freshReceivedMessageEntity.getMessageStatus());

          verify(awsQueueMessageListener, times(1)).onMessage(masterPayloadCapture.capture(),
                  headerCapture.capture(),
                  acknowledgmentArgumentCaptor.capture());
     }

     /**
      * Build a payload representing a contract create
      *
      * @return String
      */
     private String buildQueueMessagePayload() {
          String src = "{\"fresh-message-type\":\"CUSTOMER_CREATION\",\"date-created\":1602011982499,\"message-payload\":\"{\\\"id\\\":11,\\\"name\\\":\\\"Andre\\\",\\\"surname\\\":\\\"Krappie\\\",\\\"cell-number\\\":\\\"1234567890\\\",\\\"id-number\\\":\\\"223344556677\\\",\\\"email\\\":\\\"andre@vodamail.com\\\"}\",\"message-uuid\":\"b1d78aa7-ebcd-4a93-b571-4bec017b3fd2\"}\n";
          return src;
     }
}