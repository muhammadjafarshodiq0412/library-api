package com.jafarshodiq.library.controller;

import com.jafarshodiq.library.dto.response.BookResponse;
import com.jafarshodiq.library.dto.request.CreateBookRequest;
import com.jafarshodiq.library.dto.response.GenerateIsbnResponse;
import com.jafarshodiq.library.dto.response.PageResponse;
import com.jafarshodiq.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(
        name = "Books",
        description = "APIs for managing library books"
)
public class BookController {

    private final BookService service;

    @Operation(
            summary = "Register a book",
            description = "Registers a physical book copy using its ISBN and metadata."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Book registered successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
    )
    @ApiResponse(
            responseCode = "409",
            description = "ISBN metadata conflict"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse registerBook(
            @Valid @RequestBody CreateBookRequest request
    ) {
        return service.registerBook(request);
    }


    @Operation(
            summary = "List books",
            description = "Returns all registered books with their current availability."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully"
    )
    @GetMapping
    public PageResponse<BookResponse> listBooks(
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(
                    page = 1,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.listBooks(
                pageable,
                search
        );
    }

    @Operation(
            summary = "Generate ISBN",
            description = "Generates a unique ISBN-13 for a new book."
    )
    @ApiResponse(
            responseCode = "200",
            description = "ISBN generated successfully"
    )
    @GetMapping("/isbn/generate")
    public GenerateIsbnResponse generateIsbn() {
        return service.generateIsbn();
    }
}
