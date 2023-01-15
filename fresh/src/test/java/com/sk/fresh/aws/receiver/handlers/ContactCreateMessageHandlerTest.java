package com.sk.fresh.aws.receiver.handlers;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.sk.fresh.SetupTestConfiguration;
import com.sk.fresh.aws.receiver.AWSQueueMessageListener;
import com.sk.fresh.repository.FreshDetailProcessingRepository;
import com.sk.fresh.repository.FreshReceivedMessageRepository;
import com.sk.fresh.repository.entity.FreshDetailProcessingEntity;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;
import io.awspring.cloud.messaging.listener.Acknowledgment;
import io.awspring.cloud.messaging.listener.QueueMessageAcknowledgment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockserver.client.server.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@Testcontainers
@Import(SetupTestConfiguration.class)
@Tag("IntegrationTest")
@Slf4j
class ContactCreateMessageHandlerTest {

     @Autowired
     private FreshReceivedMessageRepository freshReceivedMessageRepository;

     @Autowired
     private FreshDetailProcessingRepository freshDetailProcessingRepository;

     @Autowired
     private AmazonSQSAsync amazonSQSAsync;

     @Autowired
     private AWSQueueMessageListener awsQueueMessageListener;

     /**
      * Setup the DB test container.
      */
     @Container
     public static MariaDBContainer container = new MariaDBContainer()
             .withUsername("fresh")
             .withPassword("fresh")
             .withDatabaseName("freshdesk");

     /**
      * Setup the MockServer container.
      */
     @Container
     public static MockServerContainer mockContainer = new MockServerContainer("5.10.0");

     /**
      * Setup the properties retrieved from the mock containers
      *
      * @param registry A DynamicPropertyRegistry to hold all the properties to be used by Flyway, etc.
      */
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

     /**
      * Now setup all the mocks for the MockServer to respond with the JSON Payloads.
      */
     private void setupTheMockserverExpectations() {

          MockServerClient mockServerClient = new MockServerClient(mockContainer.getHost(), mockContainer.getServerPort());

          //   Setup the first POST call to create contact. This will simulate a fail with contact already existing.
          mockServerClient.when(request().withMethod("POST")
                          .withPath("/api/v2/contacts"))
                  .respond(response().withStatusCode(409)
                          .withHeader("Content-Type", "application/json")
                          .withBody(buildContactCreatePostResponsePayload()));

          //   Setup the second call where we will get the contact using the external id of 123456789
          //   Response code should be 200 and a valid payload.
          mockServerClient.when(request()
                          .withMethod("GET")
                          .withPath("/api/v2/contacts")
                          .withQueryStringParameter("unique_external_id", "12345"))
                  .respond(response().withStatusCode(200)
                          .withHeader("Content-Type", "application/json")
                          .withBody(buildSearchResultPayload()));

          //   Setup the third call. This will simulate the update of the contact call
          mockServerClient.when(request()
                          .withMethod("PUT")
                          .withPath("/api/v2/contacts/123456789"))
                  .respond(response().withStatusCode(200)
                          .withHeader("Content-Type", "application/json")
                          .withBody(buildContactUpdatePayload()));
     }

     @BeforeEach
     void setUp() {
     }

     @Test
     void testAutoChangedFrom_contactCreate_to_contactUpdate_as_contact_exists() {

          log.info("Doing test in ContactCreateMessageHandlerTest");
          setupTheMockserverExpectations();

          String origAwsMessageId = "d2d711c5-bf93-49a8-af1f-bbcc94b05658";
          String senderId = "TestSender";

          String payload = buildQueueMessagePayload();
          Map<String, Object> allHeaders = new HashMap<>();
          allHeaders.put("MessageId", origAwsMessageId);
          allHeaders.put("SenderId", senderId);

          Acknowledgment ack = new QueueMessageAcknowledgment(amazonSQSAsync, null, null);

          awsQueueMessageListener.onMessage(payload, allHeaders, ack);

          List<FreshReceivedMessageEntity> freshReceivedMessageRepositoryList = freshReceivedMessageRepository.findAll();
          assertTrue(freshReceivedMessageRepositoryList.size() >= 1);

          FreshReceivedMessageEntity freshReceivedMessageEntity = freshReceivedMessageRepositoryList.get(0);
          assertEquals(1, freshReceivedMessageEntity.getFreshMessageId());
          assertEquals(0, freshReceivedMessageEntity.getRetryCount());
          assertEquals("CONTACT_UPDATED", freshReceivedMessageEntity.getMessageStatus().name());
          assertEquals("CUSTOMER_CREATION", freshReceivedMessageEntity.getMessageType().name());
          assertEquals("TestSender", freshReceivedMessageEntity.getOriginator());

          List<FreshDetailProcessingEntity> detailProcessingEntityList = freshDetailProcessingRepository.findAll();

          assertTrue(detailProcessingEntityList.size() >= 2);
     }


     private String buildContactCreatePostResponsePayload() {
          String src = "{\"description\":\"Validation failed\",\"errors\":[{\"field\":\"email\",\"additional_info\":{\"user_id\":123456789},\"message\":\"It should be a unique value\",\"code\":\"duplicate_value\"}]}";
          return src;
     }

     private String buildSearchResultPayload() {
          String src = "[{\"active\":false,\"address\":\"Cape Town\",\"company_id\":null,\"description\":null,\"email\":\"123456789@springfellow-hawke.co.za\",\"id\":123456789,\"job_title\":null,\"language\":\"en\",\"mobile\":\"27821234567\",\"name\":\"Andre Krappie\",\"phone\":\"27821234567\",\"time_zone\":\"Athens\",\"twitter_id\":null,\"custom_fields\":{\"customer_status\":\"ACTIVE\"},\"facebook_id\":null,\"created_at\":\"2020-09-02T19:23:13Z\",\"updated_at\":\"2020-09-03T06:46:56Z\",\"csat_rating\":null,\"preferred_source\":null,\"unique_external_id\":\"12345\",\"twitter_profile_status\":false,\"twitter_followers_count\":null}]\n";
          return src;
     }

     private String buildContactUpdatePayload() {
          String src = "{\"address\":\"Cape Town\",\"email\":\"123456789@springfellow-hawke.co.za\",\"mobile\":\"27821234567\",\"name\":\"Andre Krappie\",\"phone\":\"27821234567\",\"unique_external_id\":\"12345\",\"custom_fields\":{\"customer_status\":\"ACTIVE\"},\"tags\":[\"Tag1\"]}\n";
          return src;
     }

     private String buildQueueMessagePayload() {
          String src = "{\"fresh-message-type\":\"CUSTOMER_CREATION\",\"date-created\":1603128333817,\"message-payload\":\"{\\\"id\\\":12345,\\\"name\\\":\\\"Andre\\\",\\\"surname\\\":\\\"Krappie\\\",\\\"cell-number\\\":\\\"1234567890\\\",\\\"id-number\\\":\\\"223344556677\\\",\\\"email\\\":\\\"andre@vodamail.com\\\"}\",\"message-uuid\":\"f7f8c888-a9a6-4b4b-8b52-9d5a5c93a150\"}";
          return src;
     }
}