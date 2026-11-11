package io.github.nguyenquephong13062003.user_service.service.impl;

import io.github.nguyenquephong13062003.user_service.dto.PageResponse;
import io.github.nguyenquephong13062003.user_service.dto.UserRequest;
import io.github.nguyenquephong13062003.user_service.dto.UserResponse;
import io.github.nguyenquephong13062003.user_service.entity.User;
import io.github.nguyenquephong13062003.user_service.exception.DuplicateResourceException;
import io.github.nguyenquephong13062003.user_service.exception.ResourceNotFoundException;
import io.github.nguyenquephong13062003.user_service.mapper.UserMapper;
import io.github.nguyenquephong13062003.user_service.repository.UserRepository;
import io.github.nguyenquephong13062003.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("username '" + username + "' đã tồn tại");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("email '" + email + "' đã tồn tại");
        }

        User saved = userRepository.save(userMapper.toEntity(request));
        log.info("Đã tạo user id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAll(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable).map(userMapper::toResponse));
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = findOrThrow(id);
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new DuplicateResourceException("username '" + username + "' đã tồn tại");
        }
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("email '" + email + "' đã tồn tại");
        }

        userMapper.updateEntity(request, user);
        User saved = userRepository.save(user);
        log.info("Đã cập nhật user id={}", id);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(notFoundMessage(id));
        }
        userRepository.deleteById(id);
        log.info("Đã xóa user id={}", id);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(notFoundMessage(id)));
    }

    private String notFoundMessage(Long id) {
        return "Không tìm thấy user với id=" + id;
    }
}
