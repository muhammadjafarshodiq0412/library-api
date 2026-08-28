package com.jafarshodiq.library.service;

import com.jafarshodiq.library.entity.Book;
import com.jafarshodiq.library.entity.Borrower;
import com.jafarshodiq.library.entity.IsbnCatalog;
import com.jafarshodiq.library.entity.Loan;
import com.jafarshodiq.library.exception.ConflictException;
import com.jafarshodiq.library.exception.NotFoundException;
import com.jafarshodiq.library.repository.BookRepository;
import com.jafarshodiq.library.repository.BorrowerRepository;
import com.jafarshodiq.library.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService service;

    @Test
    void shouldBorrowBook() {

        String bookId = "book-1";
        String borrowerId = "borrower-1";

        Book book = new Book(
                new IsbnCatalog(
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin"
                )
        );
        book.setId(bookId);

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );
        borrower.setId(borrowerId);

        when(bookRepository.findByIdForUpdate(bookId))
                .thenReturn(Optional.of(book));

        when(borrowerRepository.findById(borrowerId))
                .thenReturn(Optional.of(borrower));

        when(loanRepository.existsByBookIdAndReturnedAtIsNull(bookId))
                .thenReturn(false);

        Loan loan =
                new Loan(
                        book,
                        borrower,
                        OffsetDateTime.now()
                );

        loan.setId("loan-1");

        when(loanRepository.save(any(Loan.class)))
                .thenReturn(loan);

        var result =
                service.borrow(
                        borrowerId,
                        bookId
                );

        assertNotNull(result);
        assertEquals("loan-1", result.loanId());
        assertEquals(bookId, result.bookId());
        assertEquals(borrowerId, result.borrowerId());

        verify(bookRepository)
                .findByIdForUpdate(bookId);

        verify(loanRepository)
                .save(any(Loan.class));
    }

    @Test
    void shouldRejectWhenBookNotFound() {

        when(bookRepository.findByIdForUpdate("book-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.borrow(
                        "borrower-1",
                        "book-1"
                )
        );

        verifyNoInteractions(borrowerRepository);
    }

    @Test
    void shouldRejectWhenBorrowerNotFound() {

        Book book =
                new Book(
                        new IsbnCatalog(
                                "9780132350884",
                                "Clean Code",
                                "Robert C. Martin"
                        )
                );

        when(bookRepository.findByIdForUpdate("book-1"))
                .thenReturn(Optional.of(book));

        when(borrowerRepository.findById("borrower-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.borrow(
                        "borrower-1",
                        "book-1"
                )
        );

        verify(loanRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectAlreadyBorrowedBook() {

        Book book =
                new Book(
                        new IsbnCatalog(
                                "9780132350884",
                                "Clean Code",
                                "Robert C. Martin"
                        )
                );

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );

        when(bookRepository.findByIdForUpdate("book-1"))
                .thenReturn(Optional.of(book));

        when(borrowerRepository.findById("borrower-1"))
                .thenReturn(Optional.of(borrower));

        when(loanRepository.existsByBookIdAndReturnedAtIsNull("book-1"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> service.borrow(
                        "borrower-1",
                        "book-1"
                )
        );

        verify(loanRepository, never())
                .save(any());
    }

    @Test
    void shouldReturnBook() {

        String loanId = "loan-1";
        String borrowerId = "borrower-1";

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );
        borrower.setId(borrowerId);

        Book book =
                new Book(
                        new IsbnCatalog(
                                "9780132350884",
                                "Clean Code",
                                "Robert C. Martin"
                        )
                );

        Loan loan =
                new Loan(
                        book,
                        borrower,
                        OffsetDateTime.now()
                );

        when(loanRepository.findByIdForUpdate(loanId))
                .thenReturn(Optional.of(loan));

        service.returnBook(
                borrowerId,
                loanId
        );

        assertNotNull(loan.getReturnedAt());
    }

    @Test
    void shouldRejectReturnWhenLoanNotFound() {

        when(loanRepository.findByIdForUpdate("loan-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.returnBook(
                        "borrower-1",
                        "loan-1"
                )
        );
    }

    @Test
    void shouldRejectReturnByDifferentBorrower() {

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );
        borrower.setId("borrower-owner");

        Loan loan =
                new Loan(
                        new Book(
                                new IsbnCatalog(
                                        "9780132350884",
                                        "Clean Code",
                                        "Robert C. Martin"
                                )
                        ),
                        borrower,
                        OffsetDateTime.now()
                );

        when(loanRepository.findByIdForUpdate("loan-1"))
                .thenReturn(Optional.of(loan));

        assertThrows(
                ConflictException.class,
                () -> service.returnBook(
                        "another-borrower",
                        "loan-1"
                )
        );
    }

    @Test
    void shouldRejectAlreadyReturnedLoan() {

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );
        borrower.setId("borrower-1");

        Loan loan =
                new Loan(
                        new Book(
                                new IsbnCatalog(
                                        "9780132350884",
                                        "Clean Code",
                                        "Robert C. Martin"
                                )
                        ),
                        borrower,
                        OffsetDateTime.now()
                );

        loan.setReturnedAt(
                OffsetDateTime.now()
        );

        when(loanRepository.findByIdForUpdate("loan-1"))
                .thenReturn(Optional.of(loan));

        assertThrows(
                ConflictException.class,
                () -> service.returnBook(
                        "borrower-1",
                        "loan-1"
                )
        );
    }
}