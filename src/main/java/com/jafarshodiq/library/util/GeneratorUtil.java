package com.jafarshodiq.library.util;

import java.security.SecureRandom;

public final class GeneratorUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private GeneratorUtil() {
        // Utility class
    }

    public static String generateIsbn13() {

        String prefix = "978";

        StringBuilder isbn = new StringBuilder(prefix);

        // Generate 9 random digits.
        for (int i = 0; i < 9; i++) {
            isbn.append(RANDOM.nextInt(10));
        }

        int checksum = calculateIsbn13Checksum(
                isbn.toString()
        );

        isbn.append(checksum);

        return isbn.toString();
    }

    private static int calculateIsbn13Checksum(String isbn12) {

        int sum = 0;

        for (int i = 0; i < isbn12.length(); i++) {

            int digit = Character.digit(
                    isbn12.charAt(i),
                    10
            );

            sum += (i % 2 == 0)
                    ? digit
                    : digit * 3;
        }

        return (10 - (sum % 10)) % 10;
    }

}