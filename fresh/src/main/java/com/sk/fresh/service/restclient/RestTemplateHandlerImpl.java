package com.sk.fresh.service.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.sk.fresh.service.restclient.payloads.FreshDeskRequestPayload;
import com.sk.fresh.service.restclient.payloads.FreshDeskResponsePayload;
import com.sk.fresh.service.restclient.payloads.FreshErrorResponse;
import com.sk.fresh.service.restclient.payloads.RestResponsePayload;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RestTemplateHandlerImpl implements RestTemplateHandler {

     @Value("${freshdesk.url}")
     private String url;

     private final RestTemplate restTemplate;
     private final ObjectMapper objectMapper;

     public RestTemplateHandlerImpl(RestTemplate restTemplate,
                                    ObjectMapper objectMapper) {
          this.restTemplate = restTemplate;
          this.objectMapper = objectMapper;
     }

     @Override
     public <Req extends FreshDeskRequestPayload> FreshDeskResponsePayload submitFreshdeskRequest(HttpMethod httpMethod,
                                                                                                  Req freshdeskRequestPayload,
                                                                                                  String uri) {
          var endPoint = url + (uri != null ? uri : "");
          int responseCode;

          try {
               log.debug("Sending payload : {} to endpoint {} with method {}", freshdeskRequestPayload, endPoint, httpMethod);
               var requestEntity = new HttpEntity<>(freshdeskRequestPayload, getHeaders());

               var response = restTemplate
                       .exchange(endPoint, httpMethod, requestEntity, String.class);

               responseCode = response.getStatusCode().value();
               log.debug("Response code for RestTemplate.execute : {}", responseCode);

               return buildFreshSuccessResponsePayload(response.getBody(), responseCode);
          } catch (HttpClientErrorException hc) {
               log.error("message : {}", hc.getMessage());
               log.error("RawResponseStatusCode: {}", hc.getRawStatusCode());
               log.error("ResponseBody -> {}", hc.getResponseBodyAsString());
               return buildFreshErrorResponsePayload(hc.getResponseBodyAsString(), hc.getRawStatusCode());
          } catch (Exception ex) {
               log.error("Error processing request to Freshdesk. {}", ex.getMessage(), ex);
               var errorMessage = "Internal system error";
               int rawStatusCode = ((HttpServerErrorException.InternalServerError) ex).getRawStatusCode();

               log.error("Error processing request to Freshdesk. {}", errorMessage, ex);
               return buildFreshInternalErrorResponsePayload(errorMessage, rawStatusCode);
          }
     }

     @Override
     public void deleteContact(String uri) {
          var endPoint = url + (uri != null ? uri : "");
          restTemplate.delete(endPoint);
     }

     /**
      * Build the required headers for the request to enable the correct media type.
      *
      * @return Return a new headers map with customer header fields.
      */
     private HttpHeaders getHeaders() {
          var requestHeaders = new HttpHeaders();
          List<MediaType> mediaTypeList = new ArrayList<>();
          mediaTypeList.add(MediaType.APPLICATION_JSON);
          requestHeaders.setAccept(mediaTypeList);
          requestHeaders.setContentType(MediaType.APPLICATION_JSON);
          return requestHeaders;
     }


     FreshDeskResponsePayload buildFreshErrorResponsePayload(String errorPayload, Integer responseCode) {

          try {
               var freshErrorResponse = objectMapper.readValue(errorPayload, FreshErrorResponse.class);
               freshErrorResponse.setResponseCode(responseCode);
               return freshErrorResponse;

          } catch (JsonProcessingException jpe) {
               log.error("Error parsing response payload: ", jpe);
          }
          return null;
     }

     FreshDeskResponsePayload buildFreshInternalErrorResponsePayload(String errorPayload, Integer responseCode) {
          var freshErrorResponse = new FreshErrorResponse();
          freshErrorResponse.setDescription(errorPayload);
          freshErrorResponse.setResponseCode(responseCode);
          freshErrorResponse.setErrors(new ArrayList<>());
          return freshErrorResponse;
     }

     FreshDeskResponsePayload buildFreshSuccessResponsePayload(String responseMessage, int responseCode) {
          return new RestResponsePayload(responseCode, responseMessage);
     }
}
