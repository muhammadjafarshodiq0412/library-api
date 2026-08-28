package com.jafarshodiq.library.service;

import com.jafarshodiq.library.dto.request.CreateBookRequest;
import com.jafarshodiq.library.dto.response.BookResponse;
import com.jafarshodiq.library.dto.response.GenerateIsbnResponse;
import com.jafarshodiq.library.entity.Book;
import com.jafarshodiq.library.entity.IsbnCatalog;
import com.jafarshodiq.library.exception.ConflictException;
import com.jafarshodiq.library.mapper.BookMapper;
import com.jafarshodiq.library.repository.BookRepository;
import com.jafarshodiq.library.repository.IsbnCatalogRepository;
import com.jafarshodiq.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private IsbnCatalogRepository isbnCatalogRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService service;

    private IsbnCatalog catalog;
    private Book book;

    @BeforeEach
    void setUp() {

        catalog = new IsbnCatalog(
                "9780132350884",
                "Clean Code",
                "Robert C. Martin"
        );

//        catalog.id("catalog-1");

        book = new Book(catalog);
        book.setId("book-1");
    }

    @Test
    void shouldRegisterNewBook() {

        CreateBookRequest request =
                new CreateBookRequest(
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin"
                );

        when(isbnCatalogRepository.findByIsbn(request.isbn()))
                .thenReturn(Optional.empty());

        when(isbnCatalogRepository.save(any(IsbnCatalog.class)))
                .thenReturn(catalog);

        when(bookRepository.save(any(Book.class)))
                .thenReturn(book);

        BookResponse expected =
                new BookResponse(
                        "book-1",
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin",
                        true
                );

        when(bookMapper.toResponse(book, true))
                .thenReturn(expected);

        BookResponse result =
                service.registerBook(request);

        assertEquals(expected, result);

        verify(isbnCatalogRepository)
                .findByIsbn(request.isbn());

        verify(isbnCatalogRepository)
                .save(any(IsbnCatalog.class));

        verify(bookRepository)
                .save(any(Book.class));

        verify(bookMapper)
                .toResponse(book, true);
    }

    @Test
    void shouldRegisterAnotherCopyWhenIsbnAlreadyExists() {

        CreateBookRequest request =
                new CreateBookRequest(
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin"
                );

        when(isbnCatalogRepository.findByIsbn(request.isbn()))
                .thenReturn(Optional.of(catalog));

        when(bookRepository.save(any(Book.class)))
                .thenReturn(book);

        BookResponse expected =
                new BookResponse(
                        "book-1",
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin",
                        true
                );

        when(bookMapper.toResponse(book, true))
                .thenReturn(expected);

        BookResponse result =
                service.registerBook(request);

        assertEquals(expected, result);

        verify(isbnCatalogRepository, never())
                .save(any(IsbnCatalog.class));

        verify(bookRepository)
                .save(any(Book.class));

        verify(bookMapper)
                .toResponse(book, true);
    }

    @Test
    void shouldRejectDifferentTitle() {

        when(isbnCatalogRepository.findByIsbn(catalog.getIsbn()))
                .thenReturn(Optional.of(catalog));

        CreateBookRequest request =
                new CreateBookRequest(
                        catalog.getIsbn(),
                        "Different Title",
                        catalog.getAuthor()
                );

        assertThrows(
                ConflictException.class,
                () -> service.registerBook(request)
        );

        verify(bookRepository, never())
                .save(any(Book.class));
    }

    @Test
    void shouldRejectDifferentAuthor() {

        when(isbnCatalogRepository.findByIsbn(catalog.getIsbn()))
                .thenReturn(Optional.of(catalog));

        CreateBookRequest request =
                new CreateBookRequest(
                        catalog.getIsbn(),
                        catalog.getTitle(),
                        "Different Author"
                );

        assertThrows(
                ConflictException.class,
                () -> service.registerBook(request)
        );

        verify(bookRepository, never())
                .save(any(Book.class));
    }

    @Test
    void shouldConvertDatabaseConflictToConflictException() {

        CreateBookRequest request =
                new CreateBookRequest(
                        catalog.getIsbn(),
                        catalog.getTitle(),
                        catalog.getAuthor()
                );

        when(isbnCatalogRepository.findByIsbn(request.isbn()))
                .thenReturn(Optional.of(catalog));

        when(bookRepository.save(any(Book.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "constraint"
                        )
                );

        assertThrows(
                ConflictException.class,
                () -> service.registerBook(request)
        );
    }

    @Test
    void shouldListBooks() {

        Pageable pageable =
                PageRequest.of(0, 10);

        when(bookRepository.findAll(
                ArgumentMatchers.<Specification<Book>>any(),
                eq(pageable)
        )).thenReturn(
                new PageImpl<>(
                        List.of(book),
                        pageable,
                        1
                )
        );

        when(loanRepository.existsByBookIdAndReturnedAtIsNull(
                "book-1"
        )).thenReturn(false);

        BookResponse response =
                new BookResponse(
                        "book-1",
                        catalog.getIsbn(),
                        catalog.getTitle(),
                        catalog.getAuthor(),
                        true
                );

        // available = true karena tidak ada active loan
        when(bookMapper.toResponse(book, true))
                .thenReturn(response);

        var result =
                service.listBooks(
                        pageable,
                        null
                );

        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals(response, result.content().get(0));

        verify(bookRepository).findAll(
                ArgumentMatchers.<Specification<Book>>any(),
                eq(pageable)
        );

        verify(loanRepository)
                .existsByBookIdAndReturnedAtIsNull("book-1");

        verify(bookMapper)
                .toResponse(book, true);
    }

    @Test
    void shouldGenerateUniqueIsbn() {

        when(isbnCatalogRepository.existsByIsbn(anyString()))
                .thenReturn(false);

        GenerateIsbnResponse result =
                service.generateIsbn();

        assertNotNull(result);
        assertNotNull(result.isbn());
        assertEquals(13, result.isbn().length());
        assertTrue(
                result.isbn().matches("978\\d{10}")
        );

        verify(isbnCatalogRepository)
                .existsByIsbn(result.isbn());
    }
}