package co.vellatech.vella_backend.category.dto;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Long storeId;
    private String storeName;
    private boolean active;
}
