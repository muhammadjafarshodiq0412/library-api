package com.jafarshodiq.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.jafarshodiq.library.validation.ValidIsbn13;

public record CreateBookRequest(
        @NotBlank @ValidIsbn13 String isbn,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String author
) {}
