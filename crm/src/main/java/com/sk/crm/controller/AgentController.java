package com.sk.crm.controller;

import com.sk.crm.dto.AgentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import com.sk.crm.exception.AgentNotFoundException;
import com.sk.crm.service.AgentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agents")
public class AgentController {

     private final AgentService agentService;

     @GetMapping("/{agent-id}")
     public Mono<ResponseEntity<AgentDTO>> findAgentById(@PathVariable("agent-id") Integer agentId) throws AgentNotFoundException {
          return agentService.getAgentById(agentId).map(ResponseEntity::ok)
                  .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));

     }

     @PostMapping
     public Mono<ResponseEntity<AgentDTO>> createAgent(@RequestBody AgentDTO agentDTO,
                                                       UriComponentsBuilder uriComponentsBuilder,
                                                       ServerHttpRequest req) {
          log.info("Creating new agent with payload : {}", agentDTO);
          return agentService.createAgent(agentDTO)
                  .map(agent -> ResponseEntity.created(uriComponentsBuilder.path(req.getPath() + "/{agent-id}").build(agent.getAgentId())).build());

     }

     @PutMapping("/{agent-id}")
     public Mono<ResponseEntity<AgentDTO>> updateAgent(@PathVariable("agent-id") int agentId,
                                                       @RequestBody AgentDTO agentDTO) throws AgentNotFoundException {
          log.info("Updating agent id : {} with payload : {}", agentId, agentDTO);
          return agentService.updateAgent(agentId, agentDTO)
                  .map(cus -> ResponseEntity.ok(cus));
     }
}


