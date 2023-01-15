package com.sk.crm.aws.queue;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.crm.config.AWSProperties;
import com.sk.crm.dto.AgentDTO;
import com.sk.crm.dto.CustomerDTO;
import com.sk.crm.repository.FreshMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sk.crm.aws.dto.FreshDeskPayload;
import com.sk.crm.exception.AWSException;
import com.sk.crm.repository.entity.FreshMessageType;
import com.sk.crm.repository.entity.FreshOutgoingMessage;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class QueueSubmitterServiceImpl implements QueueSubmitterService {


     private String queueUrl;

     private final AmazonSQSAsync amazonSQSAsync;
     private final ObjectMapper objectMapper;
     private final FreshMessageRepository freshMessageRepository;
     private final AWSProperties awsProperties;

     public QueueSubmitterServiceImpl(AmazonSQSAsync amazonSQSAsync,
                                      FreshMessageRepository freshMessageRepository,
                                      ObjectMapper objectMapper,
                                      AWSProperties awsProperties) {
          this.amazonSQSAsync = amazonSQSAsync;
          this.freshMessageRepository = freshMessageRepository;
          this.objectMapper = objectMapper;
          this.awsProperties = awsProperties;

          try {
               var queueName = awsProperties.getAws().getSqs().getQueueName();

               log.info("Queue name from properties is : {}", queueName);
               this.queueUrl = amazonSQSAsync.getQueueUrl(new GetQueueUrlRequest().withQueueName(queueName)).getQueueUrl();
               log.info("Queue URL build from properties is : {}", queueUrl);

          } catch (Exception ex) {
               log.error("Error setting the AWS Queue Name.", ex);
          }
     }

     @Override
     public void submitCreateMessage(CustomerDTO customerDTO) {

          try {
               var freshDeskPayload = FreshDeskPayload.builder()
                       .freshMessageType(FreshMessageType.CUSTOMER_CREATION)
                       .messagePayload(objectMapper.writeValueAsString(customerDTO))
                       .dateCreated(new Date())
                       .messageUUID(UUID.randomUUID().toString())
                       .build();

               var awsMessageId = sendMessage(freshDeskPayload);
               saveOutgoingMessage(awsMessageId, freshDeskPayload);

          } catch (JsonProcessingException je) {
               log.error("Error processing message to AWS");
          }
     }

     @Override
     public void submitUpdateMessage(CustomerDTO customerDTO) {
          try {
               var freshDeskPayload = FreshDeskPayload.builder()
                       .freshMessageType(FreshMessageType.CUSTOMER_UPDATE)
                       .messagePayload(objectMapper.writeValueAsString(customerDTO))
                       .dateCreated(new Date())
                       .messageUUID(UUID.randomUUID().toString())
                       .build();

               var awsMessageId = sendMessage(freshDeskPayload);
               saveOutgoingMessage(awsMessageId, freshDeskPayload);

          } catch (JsonProcessingException je) {
               log.error("Error processing message to AWS");
          }
     }

     @Override
     public void submitCreateMessage(AgentDTO agentDTO) {
          try {
               var freshDeskPayload = FreshDeskPayload.builder()
                       .freshMessageType(FreshMessageType.AGENT_CREATION)
                       .messagePayload(objectMapper.writeValueAsString(agentDTO))
                       .dateCreated(new Date())
                       .messageUUID(UUID.randomUUID().toString())
                       .build();

               var awsMessageId = sendMessage(freshDeskPayload);
               saveOutgoingMessage(awsMessageId, freshDeskPayload);

          } catch (JsonProcessingException je) {
               log.error("Error processing message to AWS");
          }
     }

     @Override
     public void submitUpdateMessage(AgentDTO agentDTO) {

          try {
               var freshDeskPayload = FreshDeskPayload.builder()
                       .freshMessageType(FreshMessageType.AGENT_UPDATE)
                       .messagePayload(objectMapper.writeValueAsString(agentDTO))
                       .dateCreated(new Date())
                       .messageUUID(UUID.randomUUID().toString())
                       .build();

               var awsMessageId = sendMessage(freshDeskPayload);
               saveOutgoingMessage(awsMessageId, freshDeskPayload);

          } catch (JsonProcessingException je) {
               log.error("Error processing message to AWS");
          }
     }

     private void saveOutgoingMessage(String awsMessageId, FreshDeskPayload freshDeskPayload) {

          var freshOutgoingMessage = new FreshOutgoingMessage();
          freshOutgoingMessage.setDateCreated(new Date());
          freshOutgoingMessage.setFreshMessageType(freshDeskPayload.getFreshMessageType());
          freshOutgoingMessage.setMessagePayload(freshDeskPayload.getMessagePayload());
          freshOutgoingMessage.setAwsQueueMessageId(awsMessageId);
          freshMessageRepository.save(freshOutgoingMessage);
     }

     private String sendMessage(FreshDeskPayload freshDeskPayload) {

          log.info("Sending sqsClient payload : {}", freshDeskPayload);

          try {
               var payload = objectMapper.writeValueAsString(freshDeskPayload);
               var sendMessageResult
                       = amazonSQSAsync.sendMessage(new SendMessageRequest(queueUrl, payload).withDelaySeconds(3));

               return sendMessageResult.getMessageId();

          } catch (Exception ex) {
               log.error("Error submitting message to AWS queue.", ex);
               throw new AWSException("Error submitting message to AWS queue.");
          }
     }
}
