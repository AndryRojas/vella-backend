package co.vellatech.vella_backend.category;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByStoreId(Long storeId);
    List<Category> findByStoreSlug(String storeSlug);
}