package com.saniagonsalves.storage.service;

import com.saniagonsalves.storage.model.User;
import com.saniagonsalves.storage.web.dto.UserInfoDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User save(UserInfoDto userInfoDto);

    User loadUserEntityByUsername(String username);
}
