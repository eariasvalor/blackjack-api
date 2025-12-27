package com.blackjack.application.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,                int page,                       int size,                       long totalElements,             int totalPages,                 boolean first,                  boolean last            ) {
   
    public static <T> PageResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements) {

        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                first,
                last
        );
    }
}