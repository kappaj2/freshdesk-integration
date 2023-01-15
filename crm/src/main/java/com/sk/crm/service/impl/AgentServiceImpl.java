package com.sk.crm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.sk.crm.aws.queue.QueueSubmitterService;
import com.sk.crm.dto.AgentDTO;
import com.sk.crm.exception.AgentNotFoundException;
import com.sk.crm.repository.AgentRepository;
import com.sk.crm.repository.entity.Agent;
import com.sk.crm.service.AgentService;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

     private final ModelMapper modelMapper;
     private final AgentRepository agentRepository;
     private final QueueSubmitterService queueSubmitterService;

     @Override
     public Mono<AgentDTO> getAgentById(Integer agentId) throws AgentNotFoundException {
          log.info("Searching for agent with agentId : {}", agentId);

          var agent = agentRepository.findById(agentId).orElseThrow(
                  () -> new AgentNotFoundException("Cannot find agent for agentId " + agentId)
          );

          var agentDTO = modelMapper.map(agent, AgentDTO.class);
          return Mono.just(agentDTO);
     }

     @Override
     public Mono<AgentDTO> createAgent(AgentDTO agentDTO) {

          var agent = modelMapper.map(agentDTO, Agent.class);
          agent.setDataModified(new Date());
          agent.setDateCreated(new Date());
          var savedAgent = agentRepository.save(agent);

          var createdAgentDTO = modelMapper.map(savedAgent, AgentDTO.class);
          queueSubmitterService.submitCreateMessage(createdAgentDTO);

          return Mono.just(createdAgentDTO);
     }

     @Override
     public Mono<AgentDTO> updateAgent(Integer agentId, AgentDTO agentDTO) {

          var agent = agentRepository.getOne(agentId);
          agent.setEmailAddress(agentDTO.getEmailAddress());
          var updatedAgent = agentRepository.save(agent);

          var updateAgentDTO = modelMapper.map(updatedAgent, AgentDTO.class);
          queueSubmitterService.submitUpdateMessage(updateAgentDTO);
          return Mono.just(updateAgentDTO);
     }


}
