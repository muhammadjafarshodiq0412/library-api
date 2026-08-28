package com.jafarshodiq.library.repository.spec;

import com.jafarshodiq.library.entity.Borrower;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class BorrowerSpecification {

    private BorrowerSpecification() {
    }

    public static Specification<Borrower> filter(String search) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {

                String keyword =
                        "%" + search.trim().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(
                                        cb.lower(root.get("name")),
                                        keyword
                                ),
                                cb.like(
                                        cb.lower(root.get("email")),
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
