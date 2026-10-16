package co.vellatech.vella_backend.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByStoreSlugAndEmail(String storeSlug, String email);
    boolean existsByStoreSlugAndEmail(String storeSlug, String email);
}
