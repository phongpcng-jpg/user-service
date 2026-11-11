package io.github.nguyenquephong13062003.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "fullname không được để trống")
    @Size(max = 100, message = "fullname tối đa 100 ký tự")
    private String fullname;

    @NotBlank(message = "username không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,50}$",
            message = "username dài 3-50 ký tự, chỉ gồm chữ, số, dấu chấm, gạch dưới, gạch ngang")
    private String username;

    @NotBlank(message = "email không được để trống")
    @Email(message = "email không hợp lệ")
    @Size(max = 100, message = "email tối đa 100 ký tự")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "phone phải gồm 8-15 chữ số, có thể bắt đầu bằng +")
    private String phone;

    @Size(max = 255, message = "address tối đa 255 ký tự")
    private String address;
}
