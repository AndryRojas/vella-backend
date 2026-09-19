package co.vellatech.vella_backend.category;

import co.vellatech.vella_backend.store.Store;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;           // "Cremas", "Maquillaje", "Cuidado facial"

    @Column(nullable = false)
    private String slug;           // "cremas", "maquillaje"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "is_active")
    private boolean active = true;
}