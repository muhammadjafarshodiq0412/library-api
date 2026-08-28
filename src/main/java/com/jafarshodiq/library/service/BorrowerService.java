package com.jafarshodiq.library.service;

import com.jafarshodiq.library.dto.response.BorrowerResponse;
import com.jafarshodiq.library.dto.request.CreateBorrowerRequest;
import com.jafarshodiq.library.dto.response.PageResponse;
import com.jafarshodiq.library.entity.Borrower;
import com.jafarshodiq.library.exception.ConflictException;
import com.jafarshodiq.library.mapper.BorrowerMapper;
import com.jafarshodiq.library.repository.BorrowerRepository;
import com.jafarshodiq.library.repository.spec.BorrowerSpecification;
import com.jafarshodiq.library.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final BorrowerMapper borrowerMapper;

    @Transactional
    public BorrowerResponse registerBorrower(CreateBorrowerRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        if (borrowerRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "A borrower with email already exists"
            );
        }

        try {
            Borrower saved = borrowerRepository.save(
                    new Borrower(
                            request.name().trim(),
                            email
                    )
            );

            return new BorrowerResponse(
                    saved.getId(),
                    saved.getName(),
                    saved.getEmail()
            );

        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "A borrower with email already exists"
            );
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<BorrowerResponse> listBorrowers(
            Pageable pageable,
            String search
    ) {
        Pageable zeroBasedPageable =
                PaginationUtil.toZeroBased(pageable);

        Specification<Borrower> specification =
                BorrowerSpecification.filter(search);

        Page<BorrowerResponse> borrowers = borrowerRepository
                .findAll(specification, zeroBasedPageable)
                .map(borrowerMapper::toResponse);

        return PageResponse.of(borrowers);

    }
}