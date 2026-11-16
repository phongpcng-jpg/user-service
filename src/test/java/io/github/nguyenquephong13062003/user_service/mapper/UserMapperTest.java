package io.github.nguyenquephong13062003.user_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nguyenquephong13062003.user_service.dto.UserRequest;
import io.github.nguyenquephong13062003.user_service.dto.UserResponse;
import io.github.nguyenquephong13062003.user_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    @Test
    void toEntity_trimsFieldsAndNormalizesEmail() {
        UserRequest request = UserRequest.builder()
                .fullname("  Nguyen Van A  ")
                .username("  nguyenvana ")
                .email("  A@Example.COM ")
                .phone(" 0901234567 ")
                .address(" Ho Chi Minh ")
                .build();

        User result = mapper.toEntity(request);

        assertThat(result.getFullname()).isEqualTo("Nguyen Van A");
        assertThat(result.getUsername()).isEqualTo("nguyenvana");
        assertThat(result.getEmail()).isEqualTo("a@example.com");
        assertThat(result.getPhone()).isEqualTo("0901234567");
        assertThat(result.getAddress()).isEqualTo("Ho Chi Minh");
    }

    @Test
    void updateEntity_preservesIdAndUpdatesMutableFields() {
        User user = User.builder()
                .id(10L).fullname("Old").username("old").email("old@example.com")
                .build();
        UserRequest request = UserRequest.builder()
                .fullname("New").username("new").email("NEW@example.com")
                .build();

        mapper.updateEntity(request, user);

        assertThat(user.getId()).isEqualTo(10L);
        assertThat(user.getFullname()).isEqualTo("New");
        assertThat(user.getUsername()).isEqualTo("new");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void toResponse_mapsAllFields() {
        User user = User.builder()
                .id(1L).fullname("Nguyen Van A").username("nguyenvana")
                .email("a@example.com").phone("0901234567").address("HCMC")
                .build();

        UserResponse response = mapper.toResponse(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullname()).isEqualTo("Nguyen Van A");
        assertThat(response.getUsername()).isEqualTo("nguyenvana");
        assertThat(response.getEmail()).isEqualTo("a@example.com");
        assertThat(response.getPhone()).isEqualTo("0901234567");
        assertThat(response.getAddress()).isEqualTo("HCMC");
    }

    @Test
    void updateEntity_handlesNullableFields() {
        User user = new User();
        UserRequest request = UserRequest.builder()
                .fullname("Name").username("name1").email("name@example.com")
                .build();

        mapper.updateEntity(request, user);

        assertThat(user.getPhone()).isNull();
        assertThat(user.getAddress()).isNull();
    }
}
