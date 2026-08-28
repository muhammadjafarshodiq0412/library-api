package com.jafarshodiq.library.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Isbn13ValidatorTest {

    private final Isbn13Validator validator =
            new Isbn13Validator();

    @Test
    void shouldReturnTrueForValidIsbn13() {
        assertTrue(
                validator.isValid(
                        "9780306406157",
                        null
                )
        );
    }

    @Test
    void shouldReturnFalseForInvalidChecksum() {
        assertFalse(
                validator.isValid(
                        "9780306406158",
                        null
                )
        );
    }

    @Test
    void shouldReturnFalseForNull() {
        assertFalse(
                validator.isValid(
                        null,
                        null
                )
        );
    }

    @Test
    void shouldReturnFalseForWrongLength() {
        assertFalse(
                validator.isValid(
                        "978030640615",
                        null
                )
        );
    }

    @Test
    void shouldReturnFalseForNonNumericIsbn() {
        assertFalse(
                validator.isValid(
                        "97803064061XX",
                        null
                )
        );
    }

    @Test
    void shouldReturnFalseForInvalidPrefix() {
        assertFalse(
                validator.isValid(
                        "1234567890123",
                        null
                )
        );
    }
}