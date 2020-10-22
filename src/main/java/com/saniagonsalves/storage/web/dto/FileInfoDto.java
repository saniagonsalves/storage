package com.saniagonsalves.storage.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class FileInfoDto {

    String fileName;
    String fileLink;
    Long fileId;
    LocalDateTime updateTime;
    LocalDateTime createTime;
    boolean deleted;
    String owner;
    Long ownerId;
}
