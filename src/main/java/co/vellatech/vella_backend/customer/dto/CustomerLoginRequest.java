package co.vellatech.vella_backend.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerLoginRequest {
    @NotBlank
    private String storeSlug;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
