package com.sk.fresh.service.restclient;

import org.springframework.http.HttpMethod;
import com.sk.fresh.service.restclient.payloads.FreshDeskRequestPayload;
import com.sk.fresh.service.restclient.payloads.FreshDeskResponsePayload;

public interface RestTemplateHandler {

     <Req extends FreshDeskRequestPayload> FreshDeskResponsePayload submitFreshdeskRequest(HttpMethod httpMethod,
                                                                                           Req freshDeskRequestPayload,
                                                                                           String uri);

     void deleteContact(String uri);
}
