package com.jafarshodiq.library.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path, List<FieldViolation> violations) {
    public record FieldViolation(String field, String message) {}
}
