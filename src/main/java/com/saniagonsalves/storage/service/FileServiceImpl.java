package com.saniagonsalves.storage.service;

import com.saniagonsalves.storage.model.File;
import com.saniagonsalves.storage.model.RoleEntity;
import com.saniagonsalves.storage.model.User;
import com.saniagonsalves.storage.repository.FileRepository;
import com.saniagonsalves.storage.web.dto.FileInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserService userService;

    public FileServiceImpl(FileRepository fileRepository, UserService userService) {
        super();
        this.fileRepository = fileRepository;
        this.userService = userService;
    }

    private static FileInfoDto convert(File f) {
        return FileInfoDto.builder()
            .fileLink(AWSService.cloudFrontUrl + "/" + f.getStorageId() + "/" + f.getName())
            .fileId(f.getId())
            .fileName(f.getName())
            .createTime(f.getCreateTime())
            .updateTime(f.getLastModifiedTime())
            .deleted(f.isDeleted())
            .owner(f.getOwnerId().getEmail())
            .ownerId(f.getOwnerId().getId())
            .build();
    }

    @Override
    public FileInfoDto saveFile(MultipartFile multipartFile, Principal principal) {

        User user = userService.loadUserEntityByUsername(principal.getName());

        log.info("Save file req " + user);
        String storageId = UUID.randomUUID().toString();

        String fileName;

        if (multipartFile.getOriginalFilename() != null) {
            fileName = multipartFile.getOriginalFilename().replaceAll("\\s+", "_");
        } else {
            fileName = storageId;
        }

        File fileToSave = File.builder()
            .name(fileName)
            .ownerId(user)
            .storageId(storageId)
            .build();

        boolean uploadSuccess = AWSService.uploadFile(storageId, storageId + "/" + fileName, multipartFile);

        if (uploadSuccess) {

            File uploaded = fileRepository.save(fileToSave);
            log.info("Saved " + uploaded);
            return convert(uploaded);
        } else {
            throw HttpServerErrorException.InternalServerError.create(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                      "Cannot Upload",
                                                                      HttpHeaders.EMPTY,
                                                                      null,
                                                                      null);
        }
    }

    @Override
    public FileInfoDto updateFile(MultipartFile multipartFile, long fileId, Principal principal) {

        User user = userService.loadUserEntityByUsername(principal.getName());

        log.info("Update file req " + user);
        File savedFile = fileRepository.findByIdAndOwnerIdAndDeleted(fileId, user, false);

        if (savedFile == null) {
            throw HttpServerErrorException.InternalServerError.create(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                      "File not found",
                                                                      HttpHeaders.EMPTY,
                                                                      null,
                                                                      null);
        } else {

            String fileName;

            if (multipartFile.getOriginalFilename() != null) {
                fileName = multipartFile.getOriginalFilename().replaceAll("\\s+", "_");
            } else {
                fileName = savedFile.getStorageId();
            }

            log.info("Found saved " + savedFile);
            boolean
                uploadSuccess =
                AWSService
                    .uploadFile(savedFile.getStorageId(), savedFile.getStorageId() + "/" + fileName, multipartFile);

            if (uploadSuccess) {

                savedFile.setName(fileName);

                File updated = fileRepository.save(savedFile);

                log.info("Saved " + updated);
                return convert(updated);
            } else {
                throw HttpServerErrorException.InternalServerError.create(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                          "Cannot Upload",
                                                                          HttpHeaders.EMPTY,
                                                                          null,
                                                                          null);
            }
        }
    }

    @Override
    public boolean deleteFile(long fileId, Principal principal) {

        User user = userService.loadUserEntityByUsername(principal.getName());

        log.info("Delete file req " + user);

        File savedFile = null;

        if (user.getRole() == RoleEntity.USER) {
            savedFile = fileRepository.findByIdAndOwnerIdAndDeleted(fileId, user, false);
        } else if (user.getRole() == RoleEntity.ADMIN) {
            savedFile = fileRepository.findByIdAndDeleted(fileId, false);
        }

        if (savedFile == null) {
            throw HttpServerErrorException.InternalServerError.create(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                      "File not found",
                                                                      HttpHeaders.EMPTY,
                                                                      null,
                                                                      null);
        } else {
            log.info("Found saved " + savedFile);

            savedFile.setDeleted(true);
            File updated = fileRepository.save(savedFile);

            log.info("deleted " + updated);
            return true;
        }
    }

    @Override
    public List<FileInfoDto> listFiles(Principal principal, boolean adminMode) {

        User user = userService.loadUserEntityByUsername(principal.getName());

        log.info("List files req " + user);
        List<File> files = null;

        if (user.getRole() == RoleEntity.ADMIN && adminMode) {
            files = fileRepository.findAll();
        } else {
            files = fileRepository.findByOwnerIdAndDeleted(user, false);
        }

        if (files == null || files.isEmpty()) {
            log.info("List Result Empty");
            return Collections.emptyList();
        } else {

            List<FileInfoDto> res = files
                .stream()
                .map(FileServiceImpl::convert)
                .collect(Collectors.toList());

            log.info("List Result : " + res.toString());

            return res;
        }
    }
}
