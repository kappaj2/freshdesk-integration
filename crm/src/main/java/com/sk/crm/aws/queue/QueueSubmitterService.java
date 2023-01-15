package com.sk.crm.aws.queue;

import com.sk.crm.dto.AgentDTO;
import com.sk.crm.dto.CustomerDTO;

public interface QueueSubmitterService {

     void submitCreateMessage(CustomerDTO customerDTO);
     void submitUpdateMessage(CustomerDTO customerDTO);
     void submitCreateMessage(AgentDTO agentDTO);
     void submitUpdateMessage(AgentDTO agentDTO);
}
