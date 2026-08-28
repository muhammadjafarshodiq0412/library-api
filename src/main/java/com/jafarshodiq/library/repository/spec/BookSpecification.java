package com.jafarshodiq.library.repository.spec;

import com.jafarshodiq.library.entity.Book;
import com.jafarshodiq.library.entity.IsbnCatalog;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> filter(
            String search
    ) {
        return (root, query, cb) -> {

            Join<Book, IsbnCatalog> catalog =
                    root.join("isbnCatalog", JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {

                String keyword =
                        "%" + search.trim().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(
                                        cb.lower(catalog.get("isbn")),
                                        keyword
                                ),
                                cb.like(
                                        cb.lower(catalog.get("title")),
                                        keyword
                                ),
                                cb.like(
                                        cb.lower(catalog.get("author")),
                                        keyword
                                )
                        )
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}