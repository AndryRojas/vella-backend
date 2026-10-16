package co.vellatech.vella_backend.customer;

import co.vellatech.vella_backend.auth.JwtUtil;
import co.vellatech.vella_backend.customer.dto.CustomerAuthResponse;
import co.vellatech.vella_backend.customer.dto.CustomerLoginRequest;
import co.vellatech.vella_backend.customer.dto.CustomerRegisterRequest;
import co.vellatech.vella_backend.customer.dto.CustomerResponse;
import co.vellatech.vella_backend.customer.dto.ResendCodeRequest;
import co.vellatech.vella_backend.customer.dto.VerifyEmailRequest;
import co.vellatech.vella_backend.exception.ResourceNotFoundException;
import co.vellatech.vella_backend.mail.MailService;
import co.vellatech.vella_backend.store.Store;
import co.vellatech.vella_backend.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private static final int CODE_VALIDITY_MINUTES = 15;

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;

    public CustomerResponse register(CustomerRegisterRequest request) {
        Store store = storeRepository.findBySlug(request.getStoreSlug())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tienda no encontrada: " + request.getStoreSlug()));

        if (customerRepository.existsByStoreSlugAndEmail(request.getStoreSlug(), request.getEmail())) {
            throw new RuntimeException("Ya existe una cuenta con este correo");
        }

        Customer customer = new Customer();
        customer.setStore(store);
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        assignNewVerificationCode(customer);

        customerRepository.save(customer);

        mailService.sendVerificationCode(customer.getEmail(), customer.getVerificationCode());
        mailService.sendAdminNewCustomerNotification(
                customer.getFullName(), customer.getEmail(), store.getSlug());
        log.debug("Código de verificación para {}: {}", customer.getEmail(), customer.getVerificationCode());

        return toResponse(customer);
    }

    public CustomerAuthResponse login(CustomerLoginRequest request) {
        Customer customer = customerRepository.findByStoreSlugAndEmail(request.getStoreSlug(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!customer.isActive()) {
            throw new RuntimeException("Cuenta desactivada");
        }

        if (!customer.isEmailVerified()) {
            throw new RuntimeException("Debes verificar tu correo antes de iniciar sesión");
        }

        return buildAuthResponse(customer);
    }

    public CustomerAuthResponse verifyEmail(VerifyEmailRequest request) {
        Customer customer = customerRepository.findByStoreSlugAndEmail(request.getStoreSlug(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (customer.isEmailVerified()) {
            throw new RuntimeException("La cuenta ya está verificada");
        }

        if (customer.getVerificationCode() == null
                || !customer.getVerificationCode().equals(request.getCode())
                || customer.getVerificationCodeExpiresAt() == null
                || customer.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código inválido o expirado");
        }

        customer.setEmailVerified(true);
        customer.setVerificationCode(null);
        customer.setVerificationCodeExpiresAt(null);
        customerRepository.save(customer);

        return buildAuthResponse(customer);
    }

    public void resendCode(ResendCodeRequest request) {
        Customer customer = customerRepository.findByStoreSlugAndEmail(request.getStoreSlug(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (customer.isEmailVerified()) {
            throw new RuntimeException("La cuenta ya está verificada");
        }

        assignNewVerificationCode(customer);
        customerRepository.save(customer);

        mailService.sendVerificationCode(customer.getEmail(), customer.getVerificationCode());
        log.debug("Nuevo código de verificación para {}: {}", customer.getEmail(), customer.getVerificationCode());
    }

    public CustomerResponse me(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return toResponse(customer);
    }

    private void assignNewVerificationCode(Customer customer) {
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        customer.setVerificationCode(code);
        customer.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES));
    }

    private CustomerAuthResponse buildAuthResponse(Customer customer) {
        String token = jwtUtil.generateToken(
                customer.getEmail(),
                "CUSTOMER",
                Map.of("storeId", customer.getStore().getId(), "customerId", customer.getId())
        );
        return new CustomerAuthResponse(token, toResponse(customer));
    }

    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setEmail(customer.getEmail());
        response.setFullName(customer.getFullName());
        response.setPhone(customer.getPhone());
        response.setStoreSlug(customer.getStore().getSlug());
        return response;
    }
}
