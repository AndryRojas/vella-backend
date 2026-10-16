package co.vellatech.vella_backend.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendCodeRequest {
    @NotBlank
    private String storeSlug;

    @NotBlank
    private String email;
}
