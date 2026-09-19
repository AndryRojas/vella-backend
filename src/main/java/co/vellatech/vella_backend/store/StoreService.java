package co.vellatech.vella_backend.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store findBySlug(String slug) {
        return storeRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada: " + slug));
    }
}