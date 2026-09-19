package co.vellatech.vella_backend.store;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;           // "belleza", "tecnologia", "papeleria"

    @Column(nullable = false)
    private String name;           // "Belleza & Cuidado Personal"

    private String description;

    private String domain;         // "belleza.vellatech.co"

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}