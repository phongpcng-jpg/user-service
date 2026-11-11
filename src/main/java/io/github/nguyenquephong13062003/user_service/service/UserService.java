package io.github.nguyenquephong13062003.user_service.service;

import io.github.nguyenquephong13062003.user_service.dto.PageResponse;
import io.github.nguyenquephong13062003.user_service.dto.UserRequest;
import io.github.nguyenquephong13062003.user_service.dto.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    PageResponse<UserResponse> getAll(Pageable pageable);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
