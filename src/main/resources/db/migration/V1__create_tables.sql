-- ============================================================
-- V1__create_tables.sql
-- Library API - Initial Schema
-- Database: MySQL 8+
-- ============================================================


-- ============================================================
-- ISBN CATALOG
-- Stores book metadata by ISBN
-- ============================================================

CREATE TABLE isbn_catalog (
                              isbn VARCHAR(17) NOT NULL,
                              title VARCHAR(255) NOT NULL,
                              author VARCHAR(255) NOT NULL,

                              PRIMARY KEY (isbn)
) ENGINE=InnoDB;


-- ============================================================
-- BORROWER
-- Stores library members / borrowers
-- ============================================================

CREATE TABLE borrower (
                          id VARCHAR(36) NOT NULL,
                          name VARCHAR(120) NOT NULL,
                          email VARCHAR(320) NOT NULL,

                          created_at DATETIME(6) NOT NULL,
                          updated_at DATETIME(6) NULL,
                          created_by VARCHAR(255) NULL,
                          updated_by VARCHAR(255) NULL,

                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                          PRIMARY KEY (id),

                          CONSTRAINT uk_borrower_email
                              UNIQUE (email)
) ENGINE=InnoDB;


-- ============================================================
-- BOOK
-- Physical book copy owned by the library.
--
-- Multiple book records can reference the same ISBN.
--
-- Example:
--
-- isbn_catalog
--   ISBN 9780132350884
--          |
--          +---- book #1
--          +---- book #2
--          +---- book #3
-- ============================================================

CREATE TABLE book (
                      id VARCHAR(36) NOT NULL,
                      isbn VARCHAR(17) NOT NULL,

                      created_at DATETIME(6) NOT NULL,
                      updated_at DATETIME(6) NULL,
                      created_by VARCHAR(255) NULL,
                      updated_by VARCHAR(255) NULL,

                      is_active BOOLEAN NOT NULL DEFAULT TRUE,
                      is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                      PRIMARY KEY (id),

                      CONSTRAINT fk_book_isbn
                          FOREIGN KEY (isbn)
                              REFERENCES isbn_catalog (isbn)
) ENGINE=InnoDB;


CREATE INDEX idx_book_isbn
    ON book (isbn);


-- ============================================================
-- LOAN
-- Stores book borrowing transactions.
-- ============================================================

CREATE TABLE loan (
                      id VARCHAR(36) NOT NULL,

                      book_id VARCHAR(36) NOT NULL,
                      borrower_id VARCHAR(36) NOT NULL,

                      borrowed_at DATETIME(6) NOT NULL,
                      returned_at DATETIME(6) NULL,

                      created_at DATETIME(6) NOT NULL,
                      updated_at DATETIME(6) NULL,
                      created_by VARCHAR(255) NULL,
                      updated_by VARCHAR(255) NULL,

                      is_active BOOLEAN NOT NULL DEFAULT TRUE,
                      is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                      PRIMARY KEY (id),

                      CONSTRAINT fk_loan_book
                          FOREIGN KEY (book_id)
                              REFERENCES book (id),

                      CONSTRAINT fk_loan_borrower
                          FOREIGN KEY (borrower_id)
                              REFERENCES borrower (id)
) ENGINE=InnoDB;


CREATE INDEX idx_loan_book
    ON loan (book_id);


CREATE INDEX idx_loan_borrower
    ON loan (borrower_id);


CREATE INDEX idx_loan_returned_at
    ON loan (returned_at);