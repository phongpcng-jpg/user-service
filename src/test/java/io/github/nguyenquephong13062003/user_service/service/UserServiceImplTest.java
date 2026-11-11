package io.github.nguyenquephong13062003.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.nguyenquephong13062003.user_service.dto.UserRequest;
import io.github.nguyenquephong13062003.user_service.dto.UserResponse;
import io.github.nguyenquephong13062003.user_service.entity.User;
import io.github.nguyenquephong13062003.user_service.exception.DuplicateResourceException;
import io.github.nguyenquephong13062003.user_service.exception.ResourceNotFoundException;
import io.github.nguyenquephong13062003.user_service.mapper.UserMapper;
import io.github.nguyenquephong13062003.user_service.repository.UserRepository;
import io.github.nguyenquephong13062003.user_service.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
                .fullname("Nguyen Van A")
                .username("nguyenvana")
                .email("A@Example.com")
                .phone("0901234567")
                .address("Ho Chi Minh")
                .build();
    }

    @Test
    void create_savesUserAndNormalizesEmail() {
        when(userRepository.existsByUsernameIgnoreCase("nguyenvana")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("A@Example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse result = userService.create(request());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("a@example.com");
    }

    @Test
    void create_duplicateUsername_throwsConflict() {
        when(userRepository.existsByUsernameIgnoreCase("nguyenvana")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request()))
                .isInstanceOf(DuplicateResourceException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_changesFields() {
        User existing = User.builder().id(5L).fullname("Old").username("old").email("old@x.com").build();
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsernameIgnoreCaseAndIdNot("nguyenvana", 5L)).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("A@Example.com", 5L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse result = userService.update(5L, request());

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getFullname()).isEqualTo("Nguyen Van A");
        assertThat(result.getUsername()).isEqualTo("nguyenvana");
    }

    @Test
    void delete_notFound_throws() {
        when(userRepository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(7L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(userRepository, never()).deleteById(any());
    }
}
