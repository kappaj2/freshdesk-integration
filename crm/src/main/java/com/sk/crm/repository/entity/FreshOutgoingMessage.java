package com.sk.crm.repository.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "fresh_outgoing_messages")
@Data
public class FreshOutgoingMessage {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "message_id", nullable = false)
     private Integer messageId;

     @Enumerated(EnumType.STRING)
     @Column(name = "freshdesk_message_type", nullable = false)
     private FreshMessageType freshMessageType;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_created", nullable = false)
     private Date dateCreated;

     @Column(name = "message_payload", nullable = false)
     private String messagePayload;

     @Column(name = "aws_queue_message_id")
     private String awsQueueMessageId;

     @Column(name = "message_uuid")
     private String messageUUID;

}
