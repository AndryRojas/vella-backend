package co.vellatech.vella_backend.category;

import co.vellatech.vella_backend.category.dto.CategoryRequest;
import co.vellatech.vella_backend.category.dto.CategoryResponse;
import co.vellatech.vella_backend.exception.ResourceNotFoundException;
import co.vellatech.vella_backend.store.Store;
import co.vellatech.vella_backend.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    public List<CategoryResponse> findByStoreSlug(String storeSlug) {
        return categoryRepository.findByStoreSlug(storeSlug).stream()
                .filter(Category::isActive)
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tienda no encontrada: " + request.getStoreId()));

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setStore(store);

        return toResponse(categoryRepository.save(category));
    }

    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setStoreId(category.getStore().getId());
        response.setStoreName(category.getStore().getName());
        response.setActive(category.isActive());
        return response;
    }
}
