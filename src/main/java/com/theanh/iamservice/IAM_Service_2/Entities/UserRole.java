package com.theanh.iamservice.IAM_Service_2.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_roles")
public class UserRole {

    @Id
    @GeneratedValue
    private Long id;

    private UUID userId;

    private Long roleId;
}
