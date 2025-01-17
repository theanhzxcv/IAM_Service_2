package com.theanh.iamservice.IAM_Service_2.Dtos.Response.File;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private String originalName;
    private String type;
    private String extension;
    private String owner;
    private String description;
    private boolean isPublic;

    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;
}
