package com.sk.fresh.aws.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.fresh.aws.dto.FreshDeskPayload;
import com.sk.fresh.config.AWSProperties;
import com.sk.fresh.exception.CustomerNotActiveException;
import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.exception.InternalProcessingException;
import com.sk.fresh.repository.FreshDeadLetterMessageRepository;
import com.sk.fresh.repository.FreshDetailProcessingRepository;
import com.sk.fresh.repository.FreshMessageType;
import com.sk.fresh.repository.FreshReceivedMessageRepository;
import com.sk.fresh.repository.MessageStatus;
import com.sk.fresh.repository.entity.FreshDeadLetterMessageEntity;
import com.sk.fresh.repository.entity.FreshDetailProcessingEntity;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;
import io.awspring.cloud.messaging.listener.Acknowledgment;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AWSQueueMessageListener {

     private final AWSProperties awsProperties;
     private final ObjectMapper objectMapper;
     private final List<AWSQueueMessageHandler> freshdeskQueueMessageHandlers;
     private final FreshReceivedMessageRepository freshReceivedMessageRepository;
     private final FreshDetailProcessingRepository freshDetailProcessingRepository;
     private final FreshDeadLetterMessageRepository freshDeadletterMessageRepository;

     public AWSQueueMessageListener(AWSProperties awsProperties,
                                    ObjectMapper objectMapper,
                                    List<AWSQueueMessageHandler> freshdeskQueueMessageHandlers,
                                    FreshReceivedMessageRepository freshReceivedMessageRepository,
                                    FreshDetailProcessingRepository freshDetailProcessingRepository,
                                    FreshDeadLetterMessageRepository freshDeadletterMessageRepository) {
          this.awsProperties = awsProperties;
          this.objectMapper = objectMapper;
          this.freshdeskQueueMessageHandlers = freshdeskQueueMessageHandlers;
          this.freshReceivedMessageRepository = freshReceivedMessageRepository;
          this.freshDetailProcessingRepository = freshDetailProcessingRepository;
          this.freshDeadletterMessageRepository = freshDeadletterMessageRepository;

          String uri = awsProperties.getAws().getEndPoint().getUri();
          System.setProperty("param.queue-name", uri.substring(uri.lastIndexOf("/") + 1));
     }

     @SqsListener(value = "${param.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
     public void onMessage(String masterPayload,
                           @Headers Map<String, Object> allHeaders,
                           Acknowledgment ack) {

          var awsMessageId = (String) allHeaders.get("MessageId");
          var previousAWSMessageId = (String) allHeaders.get("previousAWSMessageId");
          var senderId = (String) allHeaders.get("SenderId");

          log.info("Received awsMessageId : {}, previousMessageId : {} , senderId :{}", awsMessageId, previousAWSMessageId, senderId);

          FreshDeskPayload freshDeskPayload;
          FreshReceivedMessageEntity receivedMessageEntity;

          try {
               freshDeskPayload = objectMapper.readValue(masterPayload, FreshDeskPayload.class);
          } catch (JsonProcessingException jpe) {
               //   For now we simply acknowledge messages received on the queue and ignore.
               log.error("Error processing the masterPayload received : {}", masterPayload, jpe);
               ack.acknowledge();
               return;
          }
          log.info("Received FreshDeskPayload : {}", freshDeskPayload);

          if (previousAWSMessageId != null) {
               var receivedMessageEntityOpt
                       = freshReceivedMessageRepository.findByAwsQueueMessageId(previousAWSMessageId);
               if (receivedMessageEntityOpt.isEmpty()) {
                    log.error("Unknown message id received !!!! : {}", previousAWSMessageId);
                    ack.acknowledge();
                    return;
               }
               log.info("Reloaded entity for awsMessageId of : {}", previousAWSMessageId);
               receivedMessageEntity = receivedMessageEntityOpt.get();

          } else {
               receivedMessageEntity = insertRootEntity(awsMessageId, masterPayload, senderId, freshDeskPayload);
          }

          //   Acknowledge the message received from AWS,
          ack.acknowledge();

          /*
               Now hand of to each of the available processor for processing of the request.
           */
          freshdeskQueueMessageHandlers.forEach(processor -> {
               try {
                    processor.processMessageReceived(freshDeskPayload, receivedMessageEntity);
               } catch (FreshdeskProcessorException | InternalProcessingException | CustomerNotActiveException ine) {
                    insertErrorResponse(receivedMessageEntity, ine.getLocalizedMessage());
               }
          });
     }

     private FreshReceivedMessageEntity insertRootEntity(String awsMessageId, String masterPayload, String senderId, FreshDeskPayload freshDeskPayload) {

          var messageEntity = new FreshReceivedMessageEntity();
          messageEntity.setMessageStatus(MessageStatus.PENDING_PROCESSING);
          messageEntity.setAwsQueueMessageId(awsMessageId);
          messageEntity.setDateCreated(new Date());
          messageEntity.setDateModified(new Date());
          messageEntity.setFreshMessageId(null);
          messageEntity.setMessagePayload(masterPayload);
          messageEntity.setMessageType(FreshMessageType.valueOf(freshDeskPayload.getFreshMessageType().name()));
          messageEntity.setOriginator(senderId);
          messageEntity.setRetryCount(0);
          messageEntity.setSentTimestamp(new Date());
          messageEntity.setLastDelayTime(awsProperties.getAws().getSqs().getDelayStartSeconds());

          return freshReceivedMessageRepository.save(messageEntity);
     }

     private void insertErrorResponse(FreshReceivedMessageEntity receivedMessageEntity, String responsePayload) {

          var processingEntity = new FreshDetailProcessingEntity();
          processingEntity.setFreshReceivedMessageEntity(receivedMessageEntity);
          processingEntity.setDateCreated(new Date());
          processingEntity.setDateModified(new Date());
          processingEntity.setFreshResponseCode(500);
          processingEntity.setFreshRequestPayload("Unknown payload");
          processingEntity.setMessageType(receivedMessageEntity.getMessageType());
          processingEntity.setFreshResponsePayload(responsePayload);
          freshDetailProcessingRepository.save(processingEntity);

          var deadLetterEntity = new FreshDeadLetterMessageEntity();
          deadLetterEntity.setLastStatus(MessageStatus.SYSTEM_EXCEPTION);
          deadLetterEntity.setFreshReceivedMessageEntity(receivedMessageEntity);
          deadLetterEntity.setEmailReported(Boolean.FALSE);
          deadLetterEntity.setDateModified(new Date());
          deadLetterEntity.setDateCreated(new Date());
          freshDeadletterMessageRepository.save(deadLetterEntity);
     }
}
