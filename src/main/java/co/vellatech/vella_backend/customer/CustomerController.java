package co.vellatech.vella_backend.customer;

import co.vellatech.vella_backend.auth.AuthenticatedUser;
import co.vellatech.vella_backend.customer.dto.CustomerAuthResponse;
import co.vellatech.vella_backend.customer.dto.CustomerLoginRequest;
import co.vellatech.vella_backend.customer.dto.CustomerRegisterRequest;
import co.vellatech.vella_backend.customer.dto.CustomerResponse;
import co.vellatech.vella_backend.customer.dto.ResendCodeRequest;
import co.vellatech.vella_backend.customer.dto.VerifyEmailRequest;
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
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.ok(customerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerAuthResponse> login(@Valid @RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(customerService.login(request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<CustomerAuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(customerService.verifyEmail(request));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Void> resendCode(@Valid @RequestBody ResendCodeRequest request) {
        customerService.resendCode(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(customerService.me(principal.customerId()));
    }
}
