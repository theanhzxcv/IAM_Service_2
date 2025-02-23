package com.theanh.iamservice.IAM_Service_2.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_activity")
public class UserActivityEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String ipAddress;
    private String emailAddress;
    private String activity;
    private LocalDateTime logAt;
}
