package com.jafarshodiq.library.repository;

import com.jafarshodiq.library.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BorrowerRepository
        extends JpaRepository<Borrower, String>,
        JpaSpecificationExecutor<Borrower> {

    boolean existsByEmailIgnoreCase(String email);
}
