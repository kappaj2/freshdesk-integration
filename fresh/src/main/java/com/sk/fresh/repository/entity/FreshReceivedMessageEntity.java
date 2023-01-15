package com.sk.fresh.repository.entity;

import lombok.Getter;
import lombok.Setter;
import com.sk.fresh.repository.FreshMessageType;
import com.sk.fresh.repository.MessageStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity(name = "FreshReceivedMessageEntity")
@Table(name = "fresh_received_messages")
public class FreshReceivedMessageEntity {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "fresh_message_id", nullable = false)
     private Integer freshMessageId;

     @Column(name = "aws_queue_message_id", nullable = false)
     private String awsQueueMessageId;

     @Column(name = "message_payload", nullable = false, length = 8192)
     private String messagePayload;

     @Enumerated(EnumType.STRING)
     @Column(name = "message_type", nullable = false)
     private FreshMessageType messageType;

     @Column(name = "originator", nullable = false)
     private String originator;

     @Column(name = "retry_count", nullable = false)
     private Integer retryCount;

     @Column(name = "last_delay_time", nullable = false)
     private Integer lastDelayTime;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "sent_timestamp", nullable = false)
     private Date sentTimestamp;

     @Enumerated(EnumType.STRING)
     @Column(name = "message_status", nullable = false)
     private MessageStatus messageStatus;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_created", nullable = false)
     private Date dateCreated;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_modified")
     private Date dateModified;

     @OneToMany(
             mappedBy = "freshReceivedMessageEntity",
             cascade = CascadeType.ALL,
             orphanRemoval = true
     )
     private Set<FreshDetailProcessingEntity> freshDetailProcessingSet = new HashSet<>();


     public FreshReceivedMessageEntity() {
     }

     @Override
     public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          FreshReceivedMessageEntity that = (FreshReceivedMessageEntity) o;
          return Objects.equals(freshMessageId, that.freshMessageId) &&
                  Objects.equals(awsQueueMessageId, that.awsQueueMessageId);
     }

     @Override
     public int hashCode() {
          return Objects.hash(freshMessageId, awsQueueMessageId);
     }

     @Override
     public String toString() {
          return "FreshReceivedMessageEntity{" +
                  "freshMessageId=" + freshMessageId +
                  ", awsQueueMessageId='" + awsQueueMessageId + '\'' +
                  ", messagePayload='" + messagePayload + '\'' +
                  ", messageType=" + messageType +
                  ", originator='" + originator + '\'' +
                  ", retryCount=" + retryCount +
                  ", lastDelayTime=" + lastDelayTime +
                  ", sentTimestamp=" + sentTimestamp +
                  ", messageStatus=" + messageStatus +
                  ", dateCreated=" + dateCreated +
                  ", dateModified=" + dateModified +
                  '}';
     }
}
