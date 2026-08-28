package com.jafarshodiq.library.controller;

import com.jafarshodiq.library.dto.response.BorrowerResponse;
import com.jafarshodiq.library.dto.request.CreateBorrowerRequest;
import com.jafarshodiq.library.dto.response.PageResponse;
import com.jafarshodiq.library.service.BorrowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrowers")
@RequiredArgsConstructor
@Tag(
        name = "Borrowers",
        description = "APIs for managing library borrowers"
)
public class BorrowerController {

    private final BorrowerService service;

    @Operation(
            summary = "Register a borrower",
            description = "Registers a new borrower using a unique email address."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Borrower registered successfully"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowerResponse registerBorrower(
            @Valid @RequestBody CreateBorrowerRequest request
    ) {
        return service.registerBorrower(request);
    }

    @Operation(
            summary = "List borrowers",
            description = """
                    Returns a paginated list of borrowers.
                    The result can optionally be filtered by name or email.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Borrowers retrieved successfully"
    )
    @GetMapping
    public PageResponse<BorrowerResponse> listBorrowers(
            @Parameter(
                    description = "Search by borrower name or email",
                    example = "jafar"
            )
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(
                    page = 1,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.listBorrowers(
                pageable,
                search
        );
    }
}