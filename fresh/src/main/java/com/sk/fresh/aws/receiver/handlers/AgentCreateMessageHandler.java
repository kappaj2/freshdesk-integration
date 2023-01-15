package com.sk.fresh.aws.receiver.handlers;

import com.sk.fresh.aws.dto.FreshDeskPayload;
import com.sk.fresh.aws.receiver.AWSQueueMessageHandler;
import com.sk.fresh.exception.CustomerNotActiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sk.fresh.exception.FreshdeskProcessorException;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentCreateMessageHandler implements AWSQueueMessageHandler {
     @Override
     public void processMessageReceived(FreshDeskPayload freshdeskPayload, FreshReceivedMessageEntity freshReceivedMessageEntity) throws FreshdeskProcessorException, CustomerNotActiveException {
          log.info("Handler for agent create not implemented");
     }
}
