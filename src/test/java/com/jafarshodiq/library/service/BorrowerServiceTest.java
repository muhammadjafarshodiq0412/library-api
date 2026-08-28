package com.jafarshodiq.library.service;

import com.jafarshodiq.library.dto.request.CreateBorrowerRequest;
import com.jafarshodiq.library.dto.response.BorrowerResponse;
import com.jafarshodiq.library.entity.Borrower;
import com.jafarshodiq.library.exception.ConflictException;
import com.jafarshodiq.library.mapper.BorrowerMapper;
import com.jafarshodiq.library.repository.BorrowerRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private BorrowerMapper borrowerMapper;

    @InjectMocks
    private BorrowerService service;

    @Test
    void shouldRegisterBorrower() {

        CreateBorrowerRequest request =
                new CreateBorrowerRequest(
                        "Jafar",
                        "jafar@test.com"
                );

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );

        borrower.setId("borrower-1");

        when(borrowerRepository.existsByEmailIgnoreCase(request.email()))
                .thenReturn(false);

        when(borrowerRepository.save(any(Borrower.class)))
                .thenReturn(borrower);

        BorrowerResponse result =
                service.registerBorrower(request);

        assertNotNull(result);
        assertEquals("borrower-1", result.id());
        assertEquals("Jafar", result.name());
        assertEquals("jafar@test.com", result.email());

        verify(borrowerRepository)
                .existsByEmailIgnoreCase(request.email());

        verify(borrowerRepository)
                .save(any(Borrower.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {

        CreateBorrowerRequest request =
                new CreateBorrowerRequest(
                        "Jafar",
                        "jafar@test.com"
                );

        when(borrowerRepository.existsByEmailIgnoreCase(request.email()))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.registerBorrower(request)
        );

        assertEquals(
                "A borrower with email already exists",
                exception.getMessage()
        );

        verify(borrowerRepository)
                .existsByEmailIgnoreCase(request.email());

        verify(borrowerRepository, never())
                .save(any(Borrower.class));
    }

    @Test
    void shouldConvertDatabaseConflict() {

        CreateBorrowerRequest request =
                new CreateBorrowerRequest(
                        "Jafar",
                        "jafar@test.com"
                );

        when(borrowerRepository.existsByEmailIgnoreCase(request.email()))
                .thenReturn(false);

        when(borrowerRepository.save(any(Borrower.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "duplicate"
                        )
                );

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.registerBorrower(request)
        );

        assertNotNull(exception);

        verify(borrowerRepository)
                .existsByEmailIgnoreCase(request.email());

        verify(borrowerRepository)
                .save(any(Borrower.class));
    }

    @Test
    void shouldListBorrowersWithSearch() {

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );

        borrower.setId("borrower-1");

        Pageable pageable =
                PageRequest.of(0, 10);

        when(borrowerRepository.findAll(
                ArgumentMatchers.<Specification<Borrower>>any(),
                eq(pageable)
        )).thenReturn(
                new PageImpl<>(
                        List.of(borrower),
                        pageable,
                        1
                )
        );

        BorrowerResponse response =
                new BorrowerResponse(
                        "borrower-1",
                        "Jafar",
                        "jafar@test.com"
                );

        when(borrowerMapper.toResponse(borrower))
                .thenReturn(response);

        var result =
                service.listBorrowers(
                        pageable,
                        "jafar"
                );

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals(response, result.content().get(0));

        verify(borrowerRepository).findAll(
                ArgumentMatchers.<Specification<Borrower>>any(),
                eq(pageable)
        );

        verify(borrowerMapper)
                .toResponse(borrower);
    }

    @Test
    void shouldListBorrowersWithoutSearch() {

        Borrower borrower =
                new Borrower(
                        "Jafar",
                        "jafar@test.com"
                );

        borrower.setId("borrower-1");

        Pageable pageable =
                PageRequest.of(0, 10);

        when(borrowerRepository.findAll(
                ArgumentMatchers.<Specification<Borrower>>any(),
                eq(pageable)
        )).thenReturn(
                new PageImpl<>(
                        List.of(borrower),
                        pageable,
                        1
                )
        );

        BorrowerResponse response =
                new BorrowerResponse(
                        "borrower-1",
                        "Jafar",
                        "jafar@test.com"
                );

        when(borrowerMapper.toResponse(borrower))
                .thenReturn(response);

        var result =
                service.listBorrowers(
                        pageable,
                        null
                );

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals(response, result.content().get(0));

        verify(borrowerRepository).findAll(
                ArgumentMatchers.<Specification<Borrower>>any(),
                eq(pageable)
        );

        verify(borrowerMapper)
                .toResponse(borrower);
    }
}