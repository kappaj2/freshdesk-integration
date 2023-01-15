package com.sk.fresh.aws.receiver.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.fresh.aws.dto.CustomerDTO;
import com.sk.fresh.aws.dto.FreshDeskPayload;
import com.sk.fresh.service.crm.CRMManagementService;
import com.sk.fresh.service.restclient.payloads.FreshContact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.sk.fresh.exception.FreshdeskProcessorException;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactDTOMapper {

     private final CRMManagementService crmManagementService;
     private final ObjectMapper objectMapper;

     public Optional<FreshContact> mapDtoFromMessagePayload(FreshDeskPayload freshDeskPayload) throws FreshdeskProcessorException {

          if (freshDeskPayload.getMessagePayload().isEmpty()) {
               return Optional.empty();
          }

          try {
               var messagePayload = freshDeskPayload.getMessagePayload();
               var customerDTO = objectMapper.readValue(messagePayload, CustomerDTO.class);

               var freshContact = new FreshContact();
               freshContact.setPhone(customerDTO.getCellNumber());
               freshContact.setMobile(customerDTO.getCellNumber());
               freshContact.setName(buildConcatenatedString(customerDTO.getName(), customerDTO.getSurname()));
               freshContact.setEmail(customerDTO.getEmail());
               freshContact.setUniqueExternalId(customerDTO.getId().toString());

               /*
                    Add custom field values. These are user defined fields that is allowed by Freshdesk.
                    Set country_of_residence
                */
               var crmCountry = crmManagementService.retrieveCountryForAlpha2Code(customerDTO.getCountryCode());
               if (crmCountry.isPresent()) {
                    freshContact.getCustomFields().put("country_of_residence", crmCountry.get().getCountryDisplayName());
               }

               return Optional.of(freshContact);

          } catch (Exception ex) {
               log.error("Error processing payload received. ", ex);
               throw new FreshdeskProcessorException("Error processing payload received.");
          }
     }

     private String buildConcatenatedString(String... args) {
          var stringBuilder = new StringBuilder();

          Arrays.stream(args).forEach(en ->
                  stringBuilder.append(en).append(" ")
          );
          return stringBuilder.toString().trim();
     }
}
