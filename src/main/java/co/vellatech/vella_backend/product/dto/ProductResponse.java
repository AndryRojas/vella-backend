package co.vellatech.vella_backend.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private boolean active;
    private String storeSlug;
    private String categoryName;
    private String categorySlug;
    private List<String> imageUrls;
    private String primaryImageUrl;
}
