package com.saniagonsalves.storage.repository;

import com.saniagonsalves.storage.model.File;
import com.saniagonsalves.storage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByOwnerIdAndDeleted(User ownerId, boolean deleted);

    File findByIdAndOwnerIdAndDeleted(Long id, User ownerId, boolean deleted);

    File findByIdAndDeleted(Long id, boolean deleted);
}
