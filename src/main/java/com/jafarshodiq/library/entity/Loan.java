package com.jafarshodiq.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan")
public class Loan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_book"))
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrower_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_borrower"))
    private Borrower borrower;

    @Column(name = "borrowed_at", nullable = false)
    private OffsetDateTime borrowedAt;

    @Column(name = "returned_at")
    private OffsetDateTime returnedAt;

    public Loan(
            Book book,
            Borrower borrower,
            OffsetDateTime borrowedAt
    ) {
        this.book = book;
        this.borrower = borrower;
        this.borrowedAt = borrowedAt;
    }

}
