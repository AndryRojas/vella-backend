package co.vellatech.vella_backend.product;

import co.vellatech.vella_backend.category.Category;
import co.vellatech.vella_backend.category.CategoryRepository;
import co.vellatech.vella_backend.exception.ResourceNotFoundException;
import co.vellatech.vella_backend.product.dto.ProductRequest;
import co.vellatech.vella_backend.product.dto.ProductResponse;
import co.vellatech.vella_backend.store.Store;
import co.vellatech.vella_backend.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> findPublic(String storeSlug, String categorySlug) {
        List<Product> products = (categorySlug == null || categorySlug.isBlank())
                ? productRepository.findByStoreSlugAndActiveTrue(storeSlug)
                : productRepository.findByStoreSlugAndCategorySlugAndActiveTrue(storeSlug, categorySlug);

        return products.stream().map(this::toResponse).toList();
    }

    public ProductResponse findBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + slug));
        return toResponse(product);
    }

    public List<ProductResponse> findAllAdmin(String storeSlug) {
        return productRepository.findByStoreSlug(storeSlug).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public void deactivate(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    public void activate(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        product.setActive(true);
        productRepository.save(product);
    }

    private void applyRequest(Product product, ProductRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tienda no encontrada: " + request.getStoreId()));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoría no encontrada: " + request.getCategoryId()));
        }

        product.setName(request.getName());
        product.setSlug(request.getSlug());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStore(store);
        product.setCategory(category);

        List<ProductImage> images = new ArrayList<>();
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setUrl(request.getImageUrls().get(i));
                image.setPrimary(i == 0);
                image.setDisplayOrder(i);
                images.add(image);
            }
        }

        if (product.getImages() == null) {
            product.setImages(images);
        } else {
            product.getImages().clear();
            product.getImages().addAll(images);
        }
    }

    private ProductResponse toResponse(Product product) {
        List<ProductImage> images = product.getImages() == null
                ? List.of()
                : product.getImages().stream()
                        .sorted(Comparator.comparing(ProductImage::getDisplayOrder))
                        .toList();

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setActive(product.isActive());
        response.setStoreSlug(product.getStore().getSlug());
        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getName());
            response.setCategorySlug(product.getCategory().getSlug());
        }
        response.setImageUrls(images.stream().map(ProductImage::getUrl).toList());
        response.setPrimaryImageUrl(images.stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .or(images.stream()::findFirst)
                .map(ProductImage::getUrl)
                .orElse(null));
        return response;
    }
}
