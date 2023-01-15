package com.sk.fresh.aws.receiver.handlers;

import com.sk.fresh.aws.dto.FreshDeskPayload;
import com.sk.fresh.aws.receiver.AWSQueueMessageHandler;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;
import com.sk.fresh.service.freshdesk.ContactManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sk.fresh.aws.receiver.mapper.ContactDTOMapper;
import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.repository.FreshMessageType;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactCreateMessageHandler implements AWSQueueMessageHandler {

     private final ContactManagementService contactManagementService;
     private final ContactDTOMapper contactDTOMapper;

     @Override
     @Transactional
     public void processMessageReceived(FreshDeskPayload freshdeskPayload, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException {

          if (freshdeskPayload.getFreshMessageType() != FreshMessageType.CUSTOMER_CREATION) {
               return;
          }
          log.info("Received ContactCreate message");

          var freshContactOptional = contactDTOMapper.mapDtoFromMessagePayload(freshdeskPayload);

          if (freshContactOptional.isEmpty()) {
               log.error("Error extracting the Freshdesk Contact object from the Queue payload. ");
               throw new FreshdeskProcessorException("Unable to extract the correct payload for message id " + freshReceivedMessageEntity.getFreshMessageId());
          }

          /*
               Now create the contact by submitting the dto payload to Freshdesk
           */
          var freshContact = freshContactOptional.get();

          contactManagementService.createFreshdeskContact(freshContact, freshReceivedMessageEntity);
     }
}
