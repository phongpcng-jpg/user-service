package io.github.nguyenquephong13062003.user_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nguyenquephong13062003.user_service.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user(String username, String email) {
        return User.builder()
                .fullname("Test User").username(username).email(email)
                .build();
    }

    @Test
    void existsByUsernameIgnoreCase_findsDifferentCase() {
        userRepository.saveAndFlush(user("Phong123", "phong@example.com"));
        assertThat(userRepository.existsByUsernameIgnoreCase("PHONG123")).isTrue();
        assertThat(userRepository.existsByUsernameIgnoreCase("unknown")).isFalse();
    }

    @Test
    void existsByEmailIgnoreCase_findsDifferentCase() {
        userRepository.saveAndFlush(user("phong123", "Phong@Example.com"));
        assertThat(userRepository.existsByEmailIgnoreCase("PHONG@EXAMPLE.COM")).isTrue();
    }

    @Test
    void existsByUsernameIgnoreCaseAndIdNot_excludesCurrentUser() {
        User saved = userRepository.saveAndFlush(user("phong123", "phong@example.com"));
        assertThat(userRepository.existsByUsernameIgnoreCaseAndIdNot("PHONG123", saved.getId())).isFalse();
        assertThat(userRepository.existsByUsernameIgnoreCaseAndIdNot("PHONG123", -1L)).isTrue();
    }

    @Test
    void existsByEmailIgnoreCaseAndIdNot_excludesCurrentUser() {
        User saved = userRepository.saveAndFlush(user("phong123", "phong@example.com"));
        assertThat(userRepository.existsByEmailIgnoreCaseAndIdNot("PHONG@EXAMPLE.COM", saved.getId())).isFalse();
        assertThat(userRepository.existsByEmailIgnoreCaseAndIdNot("PHONG@EXAMPLE.COM", -1L)).isTrue();
    }

    @Test
    void findAll_supportsPagination() {
        userRepository.save(user("user001", "user1@example.com"));
        userRepository.save(user("user002", "user2@example.com"));
        userRepository.save(user("user003", "user3@example.com"));
        userRepository.flush();
        Page<User> page = userRepository.findAll(PageRequest.of(1, 2));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }
}
