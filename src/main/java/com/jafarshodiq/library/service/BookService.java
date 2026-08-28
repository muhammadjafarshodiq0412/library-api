package com.jafarshodiq.library.service;

import com.jafarshodiq.library.dto.response.BookResponse;
import com.jafarshodiq.library.dto.request.CreateBookRequest;
import com.jafarshodiq.library.dto.response.GenerateIsbnResponse;
import com.jafarshodiq.library.dto.response.PageResponse;
import com.jafarshodiq.library.entity.Book;
import com.jafarshodiq.library.entity.IsbnCatalog;
import com.jafarshodiq.library.exception.ConflictException;
import com.jafarshodiq.library.mapper.BookMapper;
import com.jafarshodiq.library.repository.BookRepository;
import com.jafarshodiq.library.repository.IsbnCatalogRepository;
import com.jafarshodiq.library.repository.LoanRepository;
import com.jafarshodiq.library.repository.spec.BookSpecification;
import com.jafarshodiq.library.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jafarshodiq.library.util.GeneratorUtil.generateIsbn13;

@Service
@RequiredArgsConstructor
public class BookService {

    private final IsbnCatalogRepository isbnCatalogRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookResponse registerBook(CreateBookRequest request) {

        String isbn = request.isbn().trim();
        String title = request.title().trim();
        String author = request.author().trim();

        IsbnCatalog catalog = isbnCatalogRepository
                .findByIsbn(isbn)
                .orElseGet(() ->
                        isbnCatalogRepository.save(
                                new IsbnCatalog(
                                        isbn,
                                        title,
                                        author
                                )
                        )
                );

        if (!catalog.getTitle().equals(title)
                || !catalog.getAuthor().equals(author)) {

            throw new ConflictException(
                    "ISBN " + isbn
                            + " is already registered with a different title/author"
            );
        }

        try {
            Book saved = bookRepository.save(
                    new Book(catalog)
            );

            return bookMapper.toResponse(
                    saved,
                    true
            );

        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Book could not be registered because of a database constraint violation"
            );
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> listBooks(
            Pageable pageable,
            String search
    ) {
        Pageable zeroBasedPageable =
                PaginationUtil.toZeroBased(pageable);

        Specification<Book> specification =
                BookSpecification.filter(
                        search
                );

        Page<BookResponse>  books = bookRepository
                .findAll(specification, zeroBasedPageable)
                .map(this::toBookResponse);

        return PageResponse.of(books);

    }

    private BookResponse toBookResponse(Book book) {

        boolean available =
                !loanRepository.existsByBookIdAndReturnedAtIsNull(
                        book.getId()
                );

        return bookMapper.toResponse(
                book,
                available
        );
    }

    @Transactional(readOnly = true)
    public GenerateIsbnResponse generateIsbn() {
        String isbn;

        do {
            isbn = generateIsbn13();
        } while (isbnCatalogRepository.existsByIsbn(isbn));

        return new GenerateIsbnResponse(isbn);
    }

}