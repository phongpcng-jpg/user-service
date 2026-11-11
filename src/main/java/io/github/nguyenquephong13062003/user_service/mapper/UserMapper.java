package io.github.nguyenquephong13062003.user_service.mapper;

import io.github.nguyenquephong13062003.user_service.dto.UserRequest;
import io.github.nguyenquephong13062003.user_service.dto.UserResponse;
import io.github.nguyenquephong13062003.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        User user = new User();
        updateEntity(request, user);
        return user;
    }

    /** Ghi đè các trường từ request vào entity có sẵn (dùng cho update). */
    public void updateEntity(UserRequest request, User user) {
        user.setFullname(trim(request.getFullname()));
        user.setUsername(trim(request.getUsername()));
        user.setEmail(trim(request.getEmail()).toLowerCase());
        user.setPhone(trim(request.getPhone()));
        user.setAddress(trim(request.getAddress()));
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
