package com.sk.crm.repository.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@Entity(name = "Agent")
@Table(name = "crm_agents")
public class Agent {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "agent_id", nullable = false)
     private Integer id;

     @Column(name = "name", nullable = false)
     private String name;

     @Column(name = "surname", nullable = false)
     private String surname;

     @Column(name = "email_address", nullable = false)
     private String emailAddress;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_modified")
     private Date dataModified;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_created")
     private Date dateCreated;

     @Column(name = "modified_by")
     private String modifiedBy;
}