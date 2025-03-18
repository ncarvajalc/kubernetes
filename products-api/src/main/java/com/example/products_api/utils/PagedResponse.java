package com.example.products_api.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PagedResponse<T>(
                List<T> content,
                long totalElements,
                int totalPages) {

        public <R> PagedResponse<R> map(Function<? super T, ? extends R> mapper) {
                List<R> mappedContent = content.stream()
                                .map(mapper)
                                .collect(Collectors.toList());
                return new PagedResponse<>(mappedContent, totalElements, totalPages);
        }
}