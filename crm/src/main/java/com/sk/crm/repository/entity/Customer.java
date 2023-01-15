package com.sk.crm.repository.entity;

import com.sk.crm.dto.CustomerStatusEnum;
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

@Data
@Entity(name = "Customer")
@Table(name = "customers")
public class Customer {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "customer_id", nullable = false)
     private Integer id;

     @Column(name = "name", nullable = false)
     private String name;

     @Column(name = "surname", nullable = false)
     private String surname;

     @Column(name = "cell_number", nullable = false)
     private String cellNumber;

     @Column(name = "id_number", nullable = false)
     private String idNumber;

     @Column(name = "email", nullable = false)
     private String email;

     @Column(name = "countryCode", nullable = false)
     private String countryCode;

     @Enumerated(EnumType.STRING)
     @Column(name = "customer_status", nullable = false)
     private CustomerStatusEnum customerStatusEnum;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_modified")
     private Date dataModified;

     @Temporal(TemporalType.TIMESTAMP)
     @Column(name = "date_created")
     private Date dateCreated;

}
