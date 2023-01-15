package com.sk.fresh.repository;

import com.sk.fresh.repository.entity.FreshDetailProcessingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface FreshDetailProcessingRepository extends JpaRepository<FreshDetailProcessingEntity, Integer> {

     @Query(value = "SELECT * FROM fresh_message_processing WHERE process_message_id = (SELECT max(process_message_id) FROM fresh_message_processing WHERE fresh_message_id = :freshMessageId)", nativeQuery = true)
     Optional<FreshDetailProcessingEntity> findLastDetailRecord(Integer freshMessageId);

     @Modifying
     @Query("delete from FreshDetailProcessingEntity u where u.dateCreated < :dateCreated")
     void deleteByDateCreatedBefore(@Param("dateCreated") Date date);
}
