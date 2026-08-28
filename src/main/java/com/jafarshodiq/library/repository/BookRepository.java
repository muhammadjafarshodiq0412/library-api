package com.jafarshodiq.library.repository;

import com.jafarshodiq.library.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository
        extends JpaRepository<Book, String>,
        JpaSpecificationExecutor<Book> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select b
           from Book b
           join fetch b.isbnCatalog
           where b.id = :id
           """)
    Optional<Book> findByIdForUpdate(
            @Param("id") String id
    );
}