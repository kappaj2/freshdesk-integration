package com.sk.fresh.repository.entity;

import lombok.Getter;
import lombok.Setter;
import com.sk.fresh.repository.MessageStatus;

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
@Entity(name = "FreshDeadLetterMessageEntity")
@Table(name = "fresh_dead_letter_messages")
public class FreshDeadLetterMessageEntity {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "fresh_failed_id", nullable = false)
     private Integer freshFailedId;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "fresh_message_id", nullable = false)
     private FreshReceivedMessageEntity freshReceivedMessageEntity;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_created", nullable = false)
     private Date dateCreated;

     @Column(name = "email_reported", nullable = false)
     private Boolean emailReported;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_modified")
     private Date dateModified;

     @Enumerated(EnumType.STRING)
     @Column(name = "last_status", nullable = false)
     private MessageStatus lastStatus;

     public FreshDeadLetterMessageEntity() {
     }

     @Override
     public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          FreshDeadLetterMessageEntity that = (FreshDeadLetterMessageEntity) o;
          return Objects.equals(freshFailedId, that.freshFailedId);
     }

     @Override
     public int hashCode() {
          return Objects.hash(freshFailedId);
     }

     @Override
     public String toString() {
          return "FreshDeadLetterMessageEntity{" +
                  "freshFailedId=" + freshFailedId +
                  ", freshReceivedMessageEntity=" + freshReceivedMessageEntity +
                  ", dateCreated=" + dateCreated +
                  ", emailReported=" + emailReported +
                  ", dateModified=" + dateModified +
                  '}';
     }
}
