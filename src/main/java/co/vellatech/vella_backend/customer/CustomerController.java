package co.vellatech.vella_backend.customer;

import co.vellatech.vella_backend.auth.AuthenticatedUser;
import co.vellatech.vella_backend.customer.dto.CustomerAuthResponse;
import co.vellatech.vella_backend.customer.dto.CustomerLoginRequest;
import co.vellatech.vella_backend.customer.dto.CustomerRegisterRequest;
import co.vellatech.vella_backend.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerAuthResponse> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.ok(customerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerAuthResponse> login(@Valid @RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(customerService.login(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(customerService.me(principal.customerId()));
    }
}
