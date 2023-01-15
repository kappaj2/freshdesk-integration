package com.sk.fresh.service.restclient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.sk.fresh.service.restclient.payloads.FreshErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@DisplayName("Test the RestTemplateHandlerImplementation class")
@Slf4j
class RestTemplateHandlerImplTest {

     @Autowired
     private RestTemplateHandlerImpl restTemplateHandler;

     @BeforeEach
     public void setup() {
     }

     @Test
     @DisplayName("Test the response builder for Freshdesk error responses")
     public void testBuildingOfFreshErrorResponse() {
          var responseCode = 409;
          var freshdeskResponsePayload = restTemplateHandler.buildFreshErrorResponsePayload(getFreshErrorResponseOne(), responseCode);

          if (freshdeskResponsePayload instanceof FreshErrorResponse) {
               var freshErrorResponse = (FreshErrorResponse) freshdeskResponsePayload;
               assertEquals("Validation failed", freshErrorResponse.getDescription());

               var errorsItemList = freshErrorResponse.getErrors();
               var errorsItem = errorsItemList.get(0);

               assertEquals("duplicate_value", errorsItem.getCode());
               assertEquals("email", errorsItem.getField());
               assertEquals("It should be a unique value", errorsItem.getMessage());
               assertEquals(responseCode, freshErrorResponse.getResponseCode());
          } else {
               fail("Wrong response object returned.");
          }
     }

     /**
      * Build a sample error response from FreshDesk
      *
      * @return
      */
     private String getFreshErrorResponseOne() {
          String src = "{\n" +
                  "  \"description\": \"Validation failed\",\n" +
                  "  \"errors\": [\n" +
                  "    {\n" +
                  "      \"field\": \"email\",\n" +
                  "      \"additional_info\": {\n" +
                  "        \"user_id\": 123456789\n" +
                  "      },\n" +
                  "      \"message\": \"It should be a unique value\",\n" +
                  "      \"code\": \"duplicate_value\"\n" +
                  "    }\n" +
                  "  ]\n" +
                  "}";
          return src;
     }


}