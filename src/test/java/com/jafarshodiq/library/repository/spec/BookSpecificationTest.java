package com.jafarshodiq.library.repository.spec;

import com.jafarshodiq.library.entity.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookSpecificationTest {

    @Test
    @SuppressWarnings("rawtypes")
    void shouldHandleEmptySearch() {

        Root<Book> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Join catalog = mock(Join.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(root.join(
                eq("isbnCatalog"),
                eq(jakarta.persistence.criteria.JoinType.INNER)
        )).thenReturn(catalog);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(finalPredicate);

        Specification<Book> specification =
                BookSpecification.filter(null);

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        cb
                );

        assertNotNull(result);
        assertSame(finalPredicate, result);

        verify(root).join(
                eq("isbnCatalog"),
                eq(jakarta.persistence.criteria.JoinType.INNER)
        );

        verify(cb).and(any(Predicate[].class));

        verify(cb, never())
                .or(any(Predicate[].class));
    }

    @Test
    @SuppressWarnings("rawtypes")
    void shouldHandleBlankSearch() {

        Root<Book> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Join catalog = mock(Join.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(root.join(
                eq("isbnCatalog"),
                eq(jakarta.persistence.criteria.JoinType.INNER)
        )).thenReturn(catalog);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(finalPredicate);

        Specification<Book> specification =
                BookSpecification.filter("   ");

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        cb
                );

        assertNotNull(result);
        assertSame(finalPredicate, result);

        verify(root).join(
                eq("isbnCatalog"),
                eq(jakarta.persistence.criteria.JoinType.INNER)
        );

        verify(cb).and(any(Predicate[].class));

        verify(cb, never())
                .or(any(Predicate[].class));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void shouldBuildSearchPredicate() {

        Root<Book> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Join catalog = mock(Join.class);

        Path isbnPath = mock(Path.class);
        Path titlePath = mock(Path.class);
        Path authorPath = mock(Path.class);

        Predicate isbnPredicate = mock(Predicate.class);
        Predicate titlePredicate = mock(Predicate.class);
        Predicate authorPredicate = mock(Predicate.class);

        Predicate orPredicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(root.join(
                eq("isbnCatalog"),
                eq(jakarta.persistence.criteria.JoinType.INNER)
        )).thenReturn(catalog);

        when(catalog.get("isbn"))
                .thenReturn(isbnPath);

        when(catalog.get("title"))
                .thenReturn(titlePath);

        when(catalog.get("author"))
                .thenReturn(authorPath);

        when(cb.lower(isbnPath))
                .thenReturn(isbnPath);

        when(cb.lower(titlePath))
                .thenReturn(titlePath);

        when(cb.lower(authorPath))
                .thenReturn(authorPath);

        when(cb.like(
                eq(isbnPath),
                eq("%java%")
        )).thenReturn(isbnPredicate);

        when(cb.like(
                eq(titlePath),
                eq("%java%")
        )).thenReturn(titlePredicate);

        when(cb.like(
                eq(authorPath),
                eq("%java%")
        )).thenReturn(authorPredicate);

        when(cb.or(
                isbnPredicate,
                titlePredicate,
                authorPredicate
        )).thenReturn(orPredicate);

        when(cb.and(orPredicate))
                .thenReturn(finalPredicate);

        Specification<Book> specification =
                BookSpecification.filter(" Java ");

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        cb
                );

        assertNotNull(result);
        assertSame(finalPredicate, result);

        verify(cb).lower(isbnPath);
        verify(cb).lower(titlePath);
        verify(cb).lower(authorPath);

        verify(cb).like(
                isbnPath,
                "%java%"
        );

        verify(cb).like(
                titlePath,
                "%java%"
        );

        verify(cb).like(
                authorPath,
                "%java%"
        );

        verify(cb).or(
                isbnPredicate,
                titlePredicate,
                authorPredicate
        );

        verify(cb).and(orPredicate);
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void shouldTrimAndLowerCaseSearchKeyword() {

        Root<Book> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Join catalog = mock(Join.class);

        Path isbnPath = mock(Path.class);
        Path titlePath = mock(Path.class);
        Path authorPath = mock(Path.class);

        Predicate isbnPredicate = mock(Predicate.class);
        Predicate titlePredicate = mock(Predicate.class);
        Predicate authorPredicate = mock(Predicate.class);

        Predicate orPredicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(root.join(
                eq("isbnCatalog"),
                eq(jakarta.persistence.criteria.JoinType.INNER)
        )).thenReturn(catalog);

        when(catalog.get("isbn"))
                .thenReturn(isbnPath);

        when(catalog.get("title"))
                .thenReturn(titlePath);

        when(catalog.get("author"))
                .thenReturn(authorPath);

        when(cb.lower(isbnPath))
                .thenReturn(isbnPath);

        when(cb.lower(titlePath))
                .thenReturn(titlePath);

        when(cb.lower(authorPath))
                .thenReturn(authorPath);

        when(cb.like(
                isbnPath,
                "%spring boot%"
        )).thenReturn(isbnPredicate);

        when(cb.like(
                titlePath,
                "%spring boot%"
        )).thenReturn(titlePredicate);

        when(cb.like(
                authorPath,
                "%spring boot%"
        )).thenReturn(authorPredicate);

        when(cb.or(
                isbnPredicate,
                titlePredicate,
                authorPredicate
        )).thenReturn(orPredicate);

        when(cb.and(orPredicate))
                .thenReturn(finalPredicate);

        Specification<Book> specification =
                BookSpecification.filter(
                        "  SPRING BOOT  "
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        cb
                );

        assertNotNull(result);
        assertSame(finalPredicate, result);

        verify(cb).like(
                isbnPath,
                "%spring boot%"
        );

        verify(cb).like(
                titlePath,
                "%spring boot%"
        );

        verify(cb).like(
                authorPath,
                "%spring boot%"
        );

        verify(cb).or(
                isbnPredicate,
                titlePredicate,
                authorPredicate
        );

        verify(cb).and(orPredicate);
    }
}