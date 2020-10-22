package com.saniagonsalves.storage.service;

import com.saniagonsalves.storage.web.dto.FileInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface FileService {

    FileInfoDto saveFile(MultipartFile multipartFile, Principal user);

    FileInfoDto updateFile(MultipartFile multipartFile, long fileId, Principal user);

    boolean deleteFile(long fileId, Principal user);

    List<FileInfoDto> listFiles(Principal user, boolean adminMode);
}
