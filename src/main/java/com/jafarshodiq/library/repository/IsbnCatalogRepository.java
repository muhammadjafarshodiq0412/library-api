package com.jafarshodiq.library.repository;

import com.jafarshodiq.library.entity.IsbnCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IsbnCatalogRepository extends JpaRepository<IsbnCatalog, String> {
    Optional<IsbnCatalog> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
}
