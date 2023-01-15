package com.sk.fresh.aws.receiver.handlers;

import com.sk.fresh.aws.dto.FreshDeskPayload;
import com.sk.fresh.aws.receiver.AWSQueueMessageHandler;
import com.sk.fresh.exception.CustomerNotActiveException;
import com.sk.fresh.service.freshdesk.ContactManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sk.fresh.aws.receiver.mapper.ContactDTOMapper;
import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.repository.FreshMessageType;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactUpdateMessageHandler implements AWSQueueMessageHandler {

     private final ContactManagementService contactManagementService;
     private final ContactDTOMapper contactDTOMapper;

     @Override
     @Transactional
     public void processMessageReceived(FreshDeskPayload freshdeskPayload, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException, CustomerNotActiveException {

          if (freshdeskPayload.getFreshMessageType() != FreshMessageType.CUSTOMER_UPDATE) {
               return;
          }
          log.info("Received ContactUpdate message");

          var freshContactOpt = contactDTOMapper.mapDtoFromMessagePayload(freshdeskPayload);
          //contactManagementService.createFreshContactDTOFromCRMPayload(freshdeskPayload);

          if (freshContactOpt.isEmpty()) {
               log.error("Error extracting the Freshdesk Contact object from the Queue payload. ");
               throw new FreshdeskProcessorException("Unable to extract the correct payload for message id " + freshReceivedMessageEntity.getFreshMessageId());
          }

          var freshContact = freshContactOpt.get();
          // Try and retrieve the contact using the unique_external_id
          log.info("Trying to retrieve contact by external id : {}", freshContact.getUniqueExternalId());

          //   Let's check if Freshdesk knows about this contact
          var freshContactViewOpt = contactManagementService.retrieveContactByExternalId(freshContact.getUniqueExternalId());

          if (!freshContactViewOpt.isPresent()) {
               //   Call create process.
               contactManagementService.createFreshdeskContact(freshContact, freshReceivedMessageEntity);

          } else {
               var freshContactView = freshContactViewOpt.get();
               //   Update existing object with new values received.
               freshContactView.setEmail(freshContact.getEmail());
               freshContactView.setMobile(freshContact.getMobile());
               freshContactView.setPhone(freshContact.getPhone());
               freshContactView.setAddress(freshContact.getAddress());

               contactManagementService.updateFreshdeskContact(freshContactView, freshContact, freshReceivedMessageEntity);
          }
     }
}
