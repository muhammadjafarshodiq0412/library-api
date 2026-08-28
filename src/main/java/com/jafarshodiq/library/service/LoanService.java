package com.jafarshodiq.library.service;

import com.jafarshodiq.library.dto.response.BorrowResponse;
import com.jafarshodiq.library.dto.response.LoanResponse;
import com.jafarshodiq.library.dto.response.PageResponse;
import com.jafarshodiq.library.entity.Book;
import com.jafarshodiq.library.entity.Borrower;
import com.jafarshodiq.library.entity.Loan;
import com.jafarshodiq.library.exception.ConflictException;
import com.jafarshodiq.library.exception.NotFoundException;
import com.jafarshodiq.library.mapper.LoanMapper;
import com.jafarshodiq.library.repository.BookRepository;
import com.jafarshodiq.library.repository.BorrowerRepository;
import com.jafarshodiq.library.repository.LoanRepository;
import com.jafarshodiq.library.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final BorrowerRepository borrowerRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;


    @Transactional(readOnly = true)
    public PageResponse<LoanResponse> listLoans(
            Pageable pageable
    ) {
        Pageable zeroBasedPageable =
                PaginationUtil.toZeroBased(pageable);

        Page<LoanResponse> loans = loanRepository
                .findAll(zeroBasedPageable)
                .map(loanMapper::toResponse);

        return PageResponse.of(loans);
    }

    @Transactional
    public BorrowResponse borrow(
            String borrowerId,
            String bookId
    ) {
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Book not found: " + bookId
                        )
                );

        // TEST ONLY: simulate slow processing while the book is locked
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new IllegalStateException("Thread was interrupted", e);
//        }

        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Borrower not found: " + borrowerId
                        )
                );

        if (loanRepository.existsByBookIdAndReturnedAtIsNull(bookId)) {
            throw new ConflictException(
                    "Book is already borrowed: " + bookId
            );
        }

        OffsetDateTime now =
                OffsetDateTime.now(ZoneOffset.UTC);

        Loan loan = new Loan(
                book,
                borrower,
                now
        );

        Loan savedLoan = loanRepository.save(loan);

        return new BorrowResponse(
                savedLoan.getId(),
                book.getId(),
                borrower.getId(),
                savedLoan.getBorrowedAt()
        );
    }

    @Transactional
    public void returnBook(
            String borrowerId,
            String loanId
    ) {

        Loan loan = loanRepository.findByIdForUpdate(loanId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Loan not found: " + loanId
                        )
                );

        if (!loan.getBorrower().getId().equals(borrowerId)) {
            throw new ConflictException(
                    "The loan does not belong to borrower: " + borrowerId
            );
        }

        if (loan.getReturnedAt() != null) {
            throw new ConflictException(
                    "Loan has already been returned: " + loanId
            );
        }

        loan.setReturnedAt(
                OffsetDateTime.now(ZoneOffset.UTC)
        );
    }

}
