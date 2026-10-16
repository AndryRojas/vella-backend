package co.vellatech.vella_backend.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerAuthResponse {
    private String token;
    private CustomerResponse customer;
}
