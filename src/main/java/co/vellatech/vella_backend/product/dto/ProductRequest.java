package co.vellatech.vella_backend.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String slug;
    private String description;
    @NotNull @DecimalMin("0.0")
    private BigDecimal price;
    private Integer stock = 0;
    @NotNull
    private Long storeId;
    private Long categoryId;
    private List<String> imageUrls;   // URLs de Cloudinary
}
