package com.sk.fresh.service.freshdesk;

import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;
import com.sk.fresh.service.restclient.payloads.FreshContact;
import com.sk.fresh.service.restclient.payloads.FreshContactView;

import java.util.List;
import java.util.Optional;

public interface ContactManagementService {

     Optional<FreshContactView> retrieveContactByEmailAddress(String emailAddress);

     Optional<FreshContactView> retrieveContactByMsisdn(String msisdn);

     Optional<FreshContactView> retrieveContactByExternalId(String externalId);

     List<FreshContactView> retrieveListOfContacts();

     void deleteAllContacts();

//     Optional<FreshContact> createFreshContactDTOFromCRMPayload(FreshDeskPayload freshDeskPayload) throws FreshdeskProcessorException, CustomerNotActiveException;

     void createFreshdeskContact(FreshContact freshContact, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException;

     void updateFreshdeskContact(FreshContactView freshContactView, FreshContact freshContact, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException;

}
