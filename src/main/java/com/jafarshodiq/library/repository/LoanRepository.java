package com.jafarshodiq.library.repository;

import com.jafarshodiq.library.entity.Loan;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, String> {

    boolean existsByBookIdAndReturnedAtIsNull(String bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT l
                FROM Loan l
                JOIN FETCH l.borrower
                JOIN FETCH l.book
                WHERE l.id = :id
            """)
    Optional<Loan> findByIdForUpdate(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT l
                FROM Loan l
                WHERE l.book.id = :bookId
                  AND l.returnedAt IS NULL
            """)
    Optional<Loan> findActiveLoanForUpdate(@Param("bookId") String bookId);

}
