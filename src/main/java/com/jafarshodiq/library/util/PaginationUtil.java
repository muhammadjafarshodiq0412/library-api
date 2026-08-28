package com.jafarshodiq.library.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static Pageable toZeroBased(Pageable pageable) {

        int page = Math.max(
                pageable.getPageNumber() - 1,
                0
        );

        return PageRequest.of(
                page,
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    public static int toOneBased(int page) {
        return page + 1;
    }
}