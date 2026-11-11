package io.github.nguyenquephong13062003.user_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private final Long id;
    private final String fullname;
    private final String username;
    private final String email;
    private final String phone;
    private final String address;
}
