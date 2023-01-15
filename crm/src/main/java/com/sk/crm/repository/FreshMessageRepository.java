package com.sk.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sk.crm.repository.entity.FreshOutgoingMessage;

@Repository
public interface FreshMessageRepository extends JpaRepository<FreshOutgoingMessage, Integer> {

}
