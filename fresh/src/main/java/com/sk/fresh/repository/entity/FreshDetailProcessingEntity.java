package com.sk.fresh.repository.entity;

import lombok.Getter;
import lombok.Setter;
import com.sk.fresh.repository.FreshMessageType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity(name = "FreshDetailProcessingEntity")
@Table(name = "fresh_message_processing")
public class FreshDetailProcessingEntity {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "process_message_id", nullable = false)
     private Integer processMessageId;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "fresh_message_id", nullable = false)
     private FreshReceivedMessageEntity freshReceivedMessageEntity;

     @Column(name = "fresh_request_payload", length = 8192)
     private String freshRequestPayload;

     @Column(name = "fresh_response_payload", length = 8192)
     private String freshResponsePayload;

     @Enumerated(EnumType.STRING)
     @Column(name = "message_type", nullable = false)
     private FreshMessageType messageType;

     @Column(name = "fresh_response_code")
     private Integer freshResponseCode;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_created", nullable = false)
     private Date dateCreated;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_modified")
     private Date dateModified;

     public FreshDetailProcessingEntity() {
     }

     @Override
     public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          FreshDetailProcessingEntity that = (FreshDetailProcessingEntity) o;
          return Objects.equals(processMessageId, that.processMessageId);
     }

     @Override
     public int hashCode() {
          return Objects.hash(processMessageId);
     }

     @Override
     public String toString() {
          return "FreshDetailProcessingEntity{" +
                  "processMessageId=" + processMessageId +
                  ", freshReceivedMessageEntity=" + freshReceivedMessageEntity +
                  ", freshRequestPayload='" + freshRequestPayload + '\'' +
                  ", freshResponsePayload='" + freshResponsePayload + '\'' +
                  ", messageType=" + messageType +
                  ", freshResponseCode=" + freshResponseCode +
                  ", dateCreated=" + dateCreated +
                  ", dateModified=" + dateModified +
                  '}';
     }
}
