package com.saniagonsalves.storage.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserInfoDto {

    String firstName;
    String lastName;
    String email;
    String password;
}
