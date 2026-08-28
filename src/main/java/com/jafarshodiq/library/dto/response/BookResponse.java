package com.jafarshodiq.library.dto.response;

public record BookResponse(
        String id,
        String isbn,
        String title,
        String author,
        boolean available
) {
}
