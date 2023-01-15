package com.sk.fresh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sk.fresh.repository.entity.FreshReceivedMessageEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FreshReceivedMessageRepository extends JpaRepository<FreshReceivedMessageEntity, Integer> {

     Optional<FreshReceivedMessageEntity> findByAwsQueueMessageId(String awsMessageId);

     List<FreshReceivedMessageEntity> findAllBySentTimestampBetween(Date fromDate, Date toDate);

     @Modifying
     @Query("delete from FreshReceivedMessageEntity u WHERE u.dateCreated < :dateCreated")
     void deleteByDateCreatedBefore(@Param("dateCreated") Date date);
}

