package com.sk.crm.service;

import com.sk.crm.dto.AgentDTO;
import reactor.core.publisher.Mono;
import com.sk.crm.exception.AgentNotFoundException;

public interface AgentService {

     Mono<AgentDTO> getAgentById(Integer id) throws AgentNotFoundException;

     Mono<AgentDTO> createAgent(AgentDTO agentDTO);

     Mono<AgentDTO> updateAgent(Integer agentId, AgentDTO agentDTO);
}
