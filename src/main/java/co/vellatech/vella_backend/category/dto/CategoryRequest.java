package co.vellatech.vella_backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String slug;
    @NotNull
    private Long storeId;
}
