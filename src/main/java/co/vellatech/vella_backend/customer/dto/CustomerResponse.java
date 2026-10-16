package co.vellatech.vella_backend.customer.dto;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String storeSlug;
}
