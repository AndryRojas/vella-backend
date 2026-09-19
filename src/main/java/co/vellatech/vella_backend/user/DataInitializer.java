package co.vellatech.vella_backend.user;

import co.vellatech.vella_backend.store.Store;
import co.vellatech.vella_backend.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Crea la tienda de belleza si no existe
        if (storeRepository.findBySlug("belleza").isEmpty()) {
            Store belleza = new Store();
            belleza.setSlug("belleza");
            belleza.setName("Belleza & Cuidado Personal");
            belleza.setDescription("Productos de belleza y cuidado de la piel");
            belleza.setDomain("belleza.vellatech.co");
            storeRepository.save(belleza);
            log.info("Tienda 'belleza' creada");
        }

        // Crea el super admin si no existe
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("VellaTech2024!"));
            admin.setRole(Role.SUPER_ADMIN);
            userRepository.save(admin);
            log.info("Usuario admin creado — password: VellaTech2024!");
        }
    }
}
