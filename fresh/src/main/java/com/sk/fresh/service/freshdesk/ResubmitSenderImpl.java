package com.sk.fresh.service.freshdesk;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sk.fresh.config.AWSProperties;
import com.sk.fresh.repository.FreshDeadLetterMessageRepository;
import com.sk.fresh.repository.FreshDetailProcessingRepository;
import com.sk.fresh.repository.FreshReceivedMessageRepository;
import com.sk.fresh.repository.MessageStatus;
import com.sk.fresh.repository.entity.FreshDeadLetterMessageEntity;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResubmitSenderImpl implements ResubmitSender {

     private final AmazonSQSAsync amazonSQSAsync;
     private final FreshReceivedMessageRepository freshReceivedMessageRepository;
     private final FreshDeadLetterMessageRepository freshDeadLetterMessageRepository;
     private final FreshDetailProcessingRepository freshDetailProcessingRepository;
     private final AWSProperties awsProperties;

     @Value("${cloud.aws.end-point.uri}")
     private String awsSqsUrl;

     /**
      * Process a resubmit request. This method is called when the create/update of contact or agent has failed.
      *
      * @param awsMessageId The original message id in the fresh_received_messages table.
      */
     @Override
     public void processResubmitRequest(String awsMessageId) {
          log.info("Doing a processResubmitRequest request for awsMessageId : {}", awsMessageId);

          var receivedMessageEntity = freshReceivedMessageRepository.findByAwsQueueMessageId(awsMessageId).get();

          /*
               Check if the last status code from Freshdesk should be re-processed. This is for rate limiting, system exception etc.
           */
          var shouldResubmit = shouldTheRecordBeReprocessed(receivedMessageEntity.getFreshMessageId());

          if (!shouldResubmit) {
               log.info("Last Freshdesk response code indicates we will not be reprocessing the request");
               receivedMessageEntity.setMessageStatus(MessageStatus.FRESH_EXCEPTION);
               receivedMessageEntity.setDateModified(new Date());
               freshReceivedMessageRepository.save(receivedMessageEntity);

               insertDeadLetter(receivedMessageEntity, MessageStatus.FRESH_EXCEPTION);
               return;
          }

          var retryCount = receivedMessageEntity.getRetryCount();

          if (retryCount >= awsProperties.getAws().getSqs().getMaxRetryCount()) {
               log.error("Reached try count limit of {} ! Stop retrying.", awsProperties.getAws().getSqs().getMaxRetryCount());
               receivedMessageEntity.setMessageStatus(MessageStatus.REACHED_RETRY_LIMIT);
               receivedMessageEntity.setDateModified(new Date());
               freshReceivedMessageRepository.save(receivedMessageEntity);

               insertDeadLetter(receivedMessageEntity, MessageStatus.REACHED_RETRY_LIMIT);
               return;
          }

          var retryDelay = receivedMessageEntity.getLastDelayTime();
          retryCount++;

          if (retryDelay > awsProperties.getAws().getSqs().getMaxDelaySeconds()) {
               retryDelay = awsProperties.getAws().getSqs().getMaxDelaySeconds();
          } else {
               retryDelay = retryDelay + awsProperties.getAws().getSqs().getRetryDelayIncrement();
          }

          receivedMessageEntity.setRetryCount(retryCount);
          receivedMessageEntity.setLastDelayTime(retryDelay);

          freshReceivedMessageRepository.save(receivedMessageEntity);

          Map<String, MessageAttributeValue> valueMap = new HashMap<>();
          valueMap.put("previousAWSMessageId", new MessageAttributeValue()
                  .withStringValue(receivedMessageEntity.getAwsQueueMessageId())
                  .withDataType("String"));

          var sendMessageRequest = new SendMessageRequest(awsSqsUrl, receivedMessageEntity.getMessagePayload())
                  .withDelaySeconds(receivedMessageEntity.getLastDelayTime())
                  .withMessageAttributes(valueMap);

          var sendMessageResult = amazonSQSAsync.sendMessage(sendMessageRequest);

          log.info("Resubmitted message id : {}", sendMessageResult.getMessageId());
     }

     public boolean shouldTheRecordBeReprocessed(Integer freshMessageId) {
          log.info("Searching for last activity record for freshMessageId : {}", freshMessageId);

          var lastDetailRecordOpt = freshDetailProcessingRepository.findLastDetailRecord(freshMessageId);
          if (lastDetailRecordOpt.isPresent()) {

               var entity = lastDetailRecordOpt.get();
               var responseCode = entity.getFreshResponseCode();

               log.info("Found last fresh response code : {}", responseCode);
               switch (responseCode) {
                    case 409: // Duplicate value exception. Contact exists.
                         return false;
                    case 400:
                         return false;  // Data validation error.
                    case 429: // Rate limit reached
                    case 500: // Network timeout, etc
                         return true;
                    default:
                         return true;
               }

          } else {
               log.error("Error checking last message action for freshMessageId : {}", freshMessageId);
               //   TODO - more handling...
               return false;
          }
     }

     private void insertDeadLetter(FreshReceivedMessageEntity receivedMessageEntity, MessageStatus messageStatus) {
          var freshDeadLetterMessageEntity = new FreshDeadLetterMessageEntity();
          freshDeadLetterMessageEntity.setDateCreated(new Date());
          freshDeadLetterMessageEntity.setDateModified(new Date());
          freshDeadLetterMessageEntity.setEmailReported(Boolean.FALSE);
          freshDeadLetterMessageEntity.setLastStatus(messageStatus);
          freshDeadLetterMessageEntity.setFreshReceivedMessageEntity(receivedMessageEntity);
          freshDeadLetterMessageRepository.save(freshDeadLetterMessageEntity);
     }
}
