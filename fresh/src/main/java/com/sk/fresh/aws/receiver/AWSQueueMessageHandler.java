package com.sk.fresh.aws.receiver;

import com.sk.fresh.aws.dto.FreshDeskPayload;
import com.sk.fresh.exception.CustomerNotActiveException;
import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;

public interface AWSQueueMessageHandler {

     void processMessageReceived(FreshDeskPayload freshdeskPayload, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException, CustomerNotActiveException;

}
