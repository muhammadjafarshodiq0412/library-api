package com.jafarshodiq.library.controller;

import com.jafarshodiq.library.dto.response.BorrowResponse;
import com.jafarshodiq.library.dto.response.LoanResponse;
import com.jafarshodiq.library.dto.response.PageResponse;
import com.jafarshodiq.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(
        name = "Loans",
        description = "APIs for borrowing and returning books"
)
public class LoanController {

    private final LoanService service;

    @Operation(
            summary = "List loans",
            description = """
                    Returns a paginated list of book borrowing transactions.
                    The result can be sorted by loan properties.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Loans retrieved successfully"
    )
    @GetMapping
    public PageResponse<LoanResponse> listLoans(
            @ParameterObject @PageableDefault(
                    page = 1,
                    sort = "borrowedAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.listLoans(pageable);
    }

    @Operation(
            summary = "Borrow a book",
            description = "Creates a new loan for a borrower. " +
                    "A physical book can only have one active loan at a time. " +
                    "Concurrent borrow requests for the same book are serialized using pessimistic locking."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Book borrowed successfully"
    )
    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowResponse borrow(
            @Parameter(description = "Unique identifier of the borrower",
                    example = "82d22c57-19d5-4e57-8c7c-489adefe62d6") @RequestParam String borrowerId,
            @Parameter(description = "Unique identifier of the book",
                    example = "7cf0b96f-013d-4522-b811-1457237408c6") @RequestParam String bookId
    ) {
        return service.borrow(borrowerId, bookId);
    }

    @Operation(
            summary = "Return a book",
            description = "Returns a borrowed book and closes its active loan."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Book returned successfully"
    )
    @PostMapping("/{loanId}/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(
            @Parameter(description = "Unique identifier of the loan") @PathVariable String loanId,
            @Parameter(description = "Unique identifier of the borrower",
                    example = "82d22c57-19d5-4e57-8c7c-489adefe62d6") @RequestParam String borrowerId
    ) {
        service.returnBook(borrowerId, loanId);
    }

}
