package io.github.nguyenquephong13062003.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.nguyenquephong13062003.user_service.dto.PageResponse;
import io.github.nguyenquephong13062003.user_service.dto.UserRequest;
import io.github.nguyenquephong13062003.user_service.dto.UserResponse;
import io.github.nguyenquephong13062003.user_service.entity.User;
import io.github.nguyenquephong13062003.user_service.exception.DuplicateResourceException;
import io.github.nguyenquephong13062003.user_service.exception.ResourceNotFoundException;
import io.github.nguyenquephong13062003.user_service.mapper.UserMapper;
import io.github.nguyenquephong13062003.user_service.repository.UserRepository;
import io.github.nguyenquephong13062003.user_service.service.impl.UserServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest request() {
        return UserRequest.builder()
                .fullname("Nguyen Van A").username("nguyenvana")
                .email("A@Example.com").phone("0901234567").address("Ho Chi Minh")
                .build();
    }

    private User user(Long id) {
        return User.builder()
                .id(id).fullname("Nguyen Van A").username("nguyenvana")
                .email("a@example.com").phone("0901234567").address("Ho Chi Minh")
                .build();
    }

    @Test
    void create_savesUserAndReturnsNormalizedResponse() {
        when(userRepository.existsByUsernameIgnoreCase("nguyenvana")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("A@Example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        UserResponse result = userService.create(request());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("a@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_duplicateUsername_throwsConflictWithoutSaving() {
        when(userRepository.existsByUsernameIgnoreCase("nguyenvana")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("username");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_duplicateEmail_throwsConflictWithoutSaving() {
        when(userRepository.existsByUsernameIgnoreCase("nguyenvana")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("A@Example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getById_existingUser_returnsResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));

        assertThat(userService.getById(1L).getUsername()).isEqualTo("nguyenvana");
    }

    @Test
    void getById_missingUser_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAll_returnsMappedPageAndMetadata() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(user(1L), user(2L)), pageable, 2));

        PageResponse<UserResponse> result = userService.getAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(UserResponse::getId).containsExactly(1L, 2L);
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    void update_existingUser_updatesFieldsAndReturnsResponse() {
        User existing = user(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsernameIgnoreCaseAndIdNot("nguyenvana", 5L)).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("A@Example.com", 5L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.update(5L, request());

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getFullname()).isEqualTo("Nguyen Van A");
        assertThat(result.getEmail()).isEqualTo("a@example.com");
    }

    @Test
    void update_duplicateUsername_throwsConflict() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user(5L)));
        when(userRepository.existsByUsernameIgnoreCaseAndIdNot("nguyenvana", 5L)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(5L, request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("username");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_duplicateEmail_throwsConflict() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user(5L)));
        when(userRepository.existsByUsernameIgnoreCaseAndIdNot("nguyenvana", 5L)).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("A@Example.com", 5L)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(5L, request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_missingUser_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, request()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_existingUser_deletesById() {
        when(userRepository.existsById(7L)).thenReturn(true);

        userService.delete(7L);

        verify(userRepository).deleteById(7L);
    }

    @Test
    void delete_missingUser_throwsNotFoundWithoutDeleting() {
        when(userRepository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(7L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(userRepository, never()).deleteById(any());
    }
}
