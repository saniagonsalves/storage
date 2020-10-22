package com.saniagonsalves.storage.service;

import com.saniagonsalves.storage.model.RoleEntity;
import com.saniagonsalves.storage.model.User;
import com.saniagonsalves.storage.repository.UserRepository;
import com.saniagonsalves.storage.web.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public User save(UserInfoDto userInfoDto) {
        User user = userRepository.save(User.builder()
                                            .firstName(userInfoDto.getFirstName())
                                            .lastName(userInfoDto.getLastName())
                                            .email(userInfoDto.getEmail())
                                            .password(passwordEncoder.encode(userInfoDto.getPassword()))
                                            .role(RoleEntity.USER)
                                            .build());

        log.info("Saved User " + user);

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " does not exist");
        }
        log.info("Found User " + user);

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().sqlValue)
            .build();
    }

    @Override
    public User loadUserEntityByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " does not exist");
        }
        log.info("Found User " + user);

        return user;
    }
}
