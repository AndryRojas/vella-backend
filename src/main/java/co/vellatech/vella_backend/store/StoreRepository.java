package co.vellatech.vella_backend.store;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findBySlug(String slug);
    Optional<Store> findByDomain(String domain);
}