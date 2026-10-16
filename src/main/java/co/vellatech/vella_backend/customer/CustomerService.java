package co.vellatech.vella_backend.customer;

import co.vellatech.vella_backend.auth.JwtUtil;
import co.vellatech.vella_backend.customer.dto.CustomerAuthResponse;
import co.vellatech.vella_backend.customer.dto.CustomerLoginRequest;
import co.vellatech.vella_backend.customer.dto.CustomerRegisterRequest;
import co.vellatech.vella_backend.customer.dto.CustomerResponse;
import co.vellatech.vella_backend.exception.ResourceNotFoundException;
import co.vellatech.vella_backend.store.Store;
import co.vellatech.vella_backend.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CustomerAuthResponse register(CustomerRegisterRequest request) {
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

        customerRepository.save(customer);

        return buildAuthResponse(customer);
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

        return buildAuthResponse(customer);
    }

    public CustomerResponse me(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return toResponse(customer);
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
