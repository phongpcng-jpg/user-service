package io.github.nguyenquephong13062003.user_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.nguyenquephong13062003.user_service.dto.PageResponse;
import io.github.nguyenquephong13062003.user_service.dto.UserRequest;
import io.github.nguyenquephong13062003.user_service.dto.UserResponse;
import io.github.nguyenquephong13062003.user_service.exception.DuplicateResourceException;
import io.github.nguyenquephong13062003.user_service.exception.GlobalExceptionHandler;
import io.github.nguyenquephong13062003.user_service.exception.ResourceNotFoundException;
import io.github.nguyenquephong13062003.user_service.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserService userService;

    private UserResponse response() {
        return UserResponse.builder().id(1L).fullname("Nguyen Van A").username("nguyenvana")
                .email("a@example.com").phone("0901234567").address("Ho Chi Minh").build();
    }

    private String validJson() {
        return """
                {"fullname":"Nguyen Van A","username":"nguyenvana","email":"a@example.com",
                 "phone":"0901234567","address":"Ho Chi Minh"}
                """;
    }

    @Test
    void create_returns201AndLocationHeader() throws Exception {
        when(userService.create(any(UserRequest.class))).thenReturn(response());
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/users/1")))
                .andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.email").value("a@example.com"));
    }

    @Test
    void create_invalidRequest_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullname\":\"\",\"username\":\"x\",\"email\":\"invalid\",\"phone\":\"abc\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{ invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_returnsPagedResponse() throws Exception {
        PageResponse<UserResponse> page = PageResponse.<UserResponse>builder().content(List.of(response()))
                .page(0).size(10).totalElements(1).totalPages(1).build();
        when(userService.getAll(any())).thenReturn(page);
        mockMvc.perform(get("/api/users").param("page", "0").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getById_existingUser_returns200() throws Exception {
        when(userService.getById(1L)).thenReturn(response());
        mockMvc.perform(get("/api/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nguyenvana"));
    }

    @Test
    void getById_missingUser_returns404() throws Exception {
        when(userService.getById(99L)).thenThrow(new ResourceNotFoundException("User not found"));
        mockMvc.perform(get("/api/users/99")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getById_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/users/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void update_returns200WithUpdatedUser() throws Exception {
        when(userService.update(eq(1L), any(UserRequest.class))).thenReturn(response());
        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_duplicateResource_returns409() throws Exception {
        when(userService.update(eq(1L), any(UserRequest.class)))
                .thenThrow(new DuplicateResourceException("username already exists"));
        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void delete_existingUser_returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());
        verify(userService).delete(1L);
    }

    @Test
    void delete_missingUser_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("User not found")).when(userService).delete(99L);
        mockMvc.perform(delete("/api/users/99")).andExpect(status().isNotFound());
    }
}
