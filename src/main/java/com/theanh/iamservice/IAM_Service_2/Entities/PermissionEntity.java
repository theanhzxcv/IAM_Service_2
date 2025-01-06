package com.theanh.iamservice.IAM_Service_2.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class PermissionEntity {
    @Id
    @Column(name = "permission_name")
    private String name;
    private String resource;
    private String scope;

    private boolean isDeleted = false;
}
