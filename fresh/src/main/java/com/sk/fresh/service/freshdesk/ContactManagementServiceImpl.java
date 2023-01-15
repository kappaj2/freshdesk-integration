package com.sk.fresh.service.freshdesk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.fresh.repository.FreshMessageType;
import com.sk.fresh.repository.FreshReceivedMessageRepository;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;
import com.sk.fresh.service.restclient.RestTemplateHandler;
import com.sk.fresh.service.restclient.payloads.FreshContact;
import com.sk.fresh.service.restclient.payloads.FreshContactView;
import com.sk.fresh.service.restclient.payloads.FreshErrorResponse;
import com.sk.fresh.service.restclient.payloads.RestResponsePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.repository.MessageStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactManagementServiceImpl extends ContactMessageRecorder implements ContactManagementService {

     private static final String ENDPOINT = "/api/v2/contacts";
     private final RestTemplateHandler restTemplateHandler;
     private final ObjectMapper objectMapper;
     private final FreshReceivedMessageRepository freshReceivedMessageRepository;
     private final ResubmitSender resubmitSender;

     @Override
     public Optional<FreshContactView> retrieveContactByEmailAddress(String emailAddress) {
          var searchEndpoint = ENDPOINT.concat("?email=").concat(emailAddress);
          return doUniqueFieldSearchCall(searchEndpoint);
     }

     @Override
     public Optional<FreshContactView> retrieveContactByMsisdn(String msisdn) {
          var searchEndpoint = ENDPOINT.concat("?phone=").concat(msisdn);
          return doUniqueFieldSearchCall(searchEndpoint);
     }

     @Override
     public Optional<FreshContactView> retrieveContactByExternalId(String externalId) {
          var searchEndpoint = ENDPOINT.concat("?unique_external_id=").concat(externalId);
          return doUniqueFieldSearchCall(searchEndpoint);
     }

     @Override
     public List<FreshContactView> retrieveListOfContacts() {
          var freshdeskResponsePayload
                  = restTemplateHandler.submitFreshdeskRequest(HttpMethod.GET, null, ENDPOINT);
          if (freshdeskResponsePayload instanceof RestResponsePayload) {
               return parseContactSearchResult((RestResponsePayload) freshdeskResponsePayload);
          } else {
               return new ArrayList<>();
          }
     }

     @Override
     public void deleteAllContacts() {

          var freshContactViewList = retrieveListOfContacts();
          log.info("Found contacts list size : {}", freshContactViewList.size());

          freshContactViewList.forEach(cont -> {
               log.info("Deleting contact with id : {} and name : {}", cont.getId(), cont.getName());
               String url = ENDPOINT + "/" + cont.getId() + "/hard_delete?force=true";
               restTemplateHandler.deleteContact(url);
          });

     }

     @Override
     public void createFreshdeskContact(FreshContact freshContact, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException {

          var freshdeskResponsePayload
                  = restTemplateHandler.submitFreshdeskRequest(HttpMethod.POST, freshContact, ENDPOINT);

          /*
               For this we should get back a 201 response code.
           */
          if (freshdeskResponsePayload instanceof FreshErrorResponse) {
               log.info("Received an error response from Freshdesk");

               var freshErrorResponse = (FreshErrorResponse) freshdeskResponsePayload;
               log.info("Error response calling create of contact : {}", freshErrorResponse);

               recordMessageProcessingDetails(freshContact, freshdeskResponsePayload, freshReceivedMessageEntity, FreshMessageType.CUSTOMER_CREATION);

               //   If it is error 409, then it is a duplicate contact. Switch to update contact mode.
               if (freshErrorResponse.getResponseCode().equals(409)) {
                    var existingFreshContactViewOpt = retrieveContactByExternalId(freshContact.getUniqueExternalId());

                    if (existingFreshContactViewOpt.isPresent()) {
                         var freshContactView = existingFreshContactViewOpt.get();
                         var id = freshContactView.getId();

                         freshdeskResponsePayload
                                 = restTemplateHandler.submitFreshdeskRequest(HttpMethod.PUT, freshContact, ENDPOINT + "/" + id);
                         recordMessageProcessingDetails(freshContact, freshdeskResponsePayload, freshReceivedMessageEntity, FreshMessageType.CUSTOMER_UPDATE);

                         /*
                              If this was a success - then update master as
                          */
                         if (freshdeskResponsePayload instanceof RestResponsePayload) {
                              freshReceivedMessageEntity.setMessageStatus(MessageStatus.CONTACT_UPDATED);
                              freshReceivedMessageEntity.setDateModified(new Date());
                              freshReceivedMessageRepository.save(freshReceivedMessageEntity);

                              // response code of 200 indicates successful update
                              if (((RestResponsePayload) freshdeskResponsePayload).getResponseCode() < 300) {
                                   return;
                              }
                         }
                    }
               }

               /*
                    Retry if the response code is not in the 200 range.
                    This will check for rate limit errors so we can backoff and retry again.
                    Also handle possible network error responses, etc.
                */
               if (freshErrorResponse.getResponseCode() >= 300) {
                    log.info("Total failure for create / update. Doing processResubmitRequest");
                    resubmitSender.processResubmitRequest(freshReceivedMessageEntity.getAwsQueueMessageId());
               }
          } else {
               log.info("Received success response from Freshdesk");
               // success - update master record.
               freshReceivedMessageEntity.setMessageStatus(MessageStatus.CONTACT_CREATED);
               freshReceivedMessageEntity.setDateModified(new Date());
               freshReceivedMessageRepository.save(freshReceivedMessageEntity);

               recordMessageProcessingDetails(freshContact, freshdeskResponsePayload, freshReceivedMessageEntity, FreshMessageType.CUSTOMER_CREATION);
          }
     }

     @Override
     public void updateFreshdeskContact(FreshContactView freshContactView, FreshContact freshContact, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException {

          var freshdeskResponsePayload
                  = restTemplateHandler.submitFreshdeskRequest(HttpMethod.PUT, freshContact, ENDPOINT + "/" + freshContactView.getId());

          //   Error response
          if (freshdeskResponsePayload instanceof FreshErrorResponse) {

               var freshErrorResponse = (FreshErrorResponse) freshdeskResponsePayload;
               log.info("Error response calling update of customer : {}", freshErrorResponse);

               recordMessageProcessingDetails(freshContact, freshdeskResponsePayload, freshReceivedMessageEntity, FreshMessageType.CUSTOMER_UPDATE);

                    /*
                         Retry if the response code is not in the 200 range.
                    */
               if (freshErrorResponse.getResponseCode() >= 300) {
                    resubmitSender.processResubmitRequest(freshReceivedMessageEntity.getAwsQueueMessageId());
               }

          } else { // success response
               // success - update master record.
               freshReceivedMessageEntity.setMessageStatus(MessageStatus.CONTACT_UPDATED);
               freshReceivedMessageEntity.setDateModified(new Date());
               freshReceivedMessageRepository.save(freshReceivedMessageEntity);

               recordMessageProcessingDetails(freshContact, freshdeskResponsePayload, freshReceivedMessageEntity, FreshMessageType.CUSTOMER_UPDATE);
          }
     }

     private Optional<FreshContactView> doUniqueFieldSearchCall(String searchEndpoint) {

          var freshdeskResponsePayload
                  = restTemplateHandler.submitFreshdeskRequest(HttpMethod.GET, null, searchEndpoint);

          if (freshdeskResponsePayload instanceof RestResponsePayload) {
               var restResponsePayload = (RestResponsePayload) freshdeskResponsePayload;
               var freshContactViewList = parseContactSearchResult(restResponsePayload);
               if (!freshContactViewList.isEmpty()) {
                    return Optional.of(freshContactViewList.get(0));
               }
          }
          return Optional.empty();
     }

     private List<FreshContactView> parseContactSearchResult(RestResponsePayload restResponsePayload) {
          try {
               var freshContactViewArr = objectMapper.readValue(restResponsePayload.getResponseMessage(), FreshContactView[].class);
               return Arrays.asList(freshContactViewArr);

          } catch (JsonProcessingException jp) {
               log.error("Error processing response message", jp);
          }
          return new ArrayList<>();
     }
}
