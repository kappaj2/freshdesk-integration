package com.sk.fresh.repository;

import com.sk.fresh.repository.entity.FreshDeadLetterMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FreshDeadLetterMessageRepository extends JpaRepository<FreshDeadLetterMessageEntity, Integer> {

     List<FreshDeadLetterMessageEntity> findByEmailReportedIsFalse();

     @Modifying
     @Query("delete from FreshDeadLetterMessageEntity u WHERE u.dateCreated < :dateCreated")
     void deleteByDateCreatedBefore(@Param("dateCreated") Date date);
}
