package com.sk.fresh.service.freshdesk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.fresh.repository.FreshDetailProcessingRepository;
import com.sk.fresh.repository.FreshMessageType;
import com.sk.fresh.repository.entity.FreshDetailProcessingEntity;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;
import com.sk.fresh.service.restclient.payloads.FreshErrorResponse;
import com.sk.fresh.service.restclient.payloads.RestResponsePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.service.restclient.payloads.FreshContact;
import com.sk.fresh.service.restclient.payloads.FreshDeskResponsePayload;

import java.util.Date;

@Slf4j
public abstract class ContactMessageRecorder {

     private FreshDetailProcessingRepository freshDetailProcessingRepository;
     private ObjectMapper objectMapper;

     @Autowired
     public void setFreshDetailProcessingRepository(FreshDetailProcessingRepository freshDetailProcessingRepository) {
          this.freshDetailProcessingRepository = freshDetailProcessingRepository;
     }

     @Autowired
     public void setObjectMapper(ObjectMapper objectMapper) {
          this.objectMapper = objectMapper;
     }


     protected void recordMessageProcessingDetails(FreshContact freshContact,
                                                   FreshDeskResponsePayload freshdeskResponsePayload,
                                                   FreshReceivedMessageEntity freshReceivedMessageEntity,
                                                   FreshMessageType freshMessageType) throws FreshdeskProcessorException {

          var freshDetailProcessingEntity = new FreshDetailProcessingEntity();

          try {
               freshDetailProcessingEntity.setDateCreated(new Date());
               freshDetailProcessingEntity.setDateModified(new Date());
               freshDetailProcessingEntity.setFreshReceivedMessageEntity(freshReceivedMessageEntity);
               freshDetailProcessingEntity.setMessageType(freshMessageType);
               freshDetailProcessingEntity.setFreshRequestPayload(objectMapper.writeValueAsString(freshContact));

               //   Populate possible error value
               if (freshdeskResponsePayload instanceof FreshErrorResponse) {
                    var freshErrorResponse = (FreshErrorResponse) freshdeskResponsePayload;
                    freshDetailProcessingEntity.setFreshResponsePayload(objectMapper.writeValueAsString(freshErrorResponse));
                    freshDetailProcessingEntity.setFreshResponseCode(freshErrorResponse.getResponseCode());
               } else if (freshdeskResponsePayload instanceof RestResponsePayload) {
                    var freshSuccessResponse = (RestResponsePayload) freshdeskResponsePayload;
                    freshDetailProcessingEntity.setFreshResponsePayload(objectMapper.writeValueAsString(freshSuccessResponse));
                    freshDetailProcessingEntity.setFreshResponseCode(freshSuccessResponse.getResponseCode());
               }
               freshDetailProcessingRepository.save(freshDetailProcessingEntity);

          } catch (JsonProcessingException jpe) {
               log.error("Error processing the detail record. {}", jpe.getMessage());
               throw new FreshdeskProcessorException("Error processing the detail record create. ");
          }
     }
}
