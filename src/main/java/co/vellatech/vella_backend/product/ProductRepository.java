package co.vellatech.vella_backend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreSlugAndActiveTrue(String storeSlug);
    List<Product> findByStoreSlugAndCategorySlugAndActiveTrue(String storeSlug, String categorySlug);
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
    List<Product> findByStoreSlug(String storeSlug);
    Optional<Product> findBySlug(String slug);
}