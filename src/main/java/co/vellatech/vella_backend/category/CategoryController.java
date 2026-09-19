package co.vellatech.vella_backend.category;

import co.vellatech.vella_backend.category.dto.CategoryRequest;
import co.vellatech.vella_backend.category.dto.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Público
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getByStore(
            @RequestParam String store) {
        return ResponseEntity.ok(categoryService.findByStoreSlug(store));
    }

    // Protegido
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STORE_ADMIN')")
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.create(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STORE_ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        categoryService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
