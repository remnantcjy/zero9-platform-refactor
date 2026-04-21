package com.zero9platform.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@NoArgsConstructor  // 역직렬화를 위해 기본 생성자 유지
public class PageResponse<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;

    public PageResponse(List<T> content, long totalElements, int totalPages, int size, int number) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.size = size;
        this.number = number;
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }

    public static <T> PageResponse<T> of(List<T> content, Pageable pageable, long totalElements) {
        int size = pageable.getPageSize();
        int number = pageable.getPageNumber();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                totalElements,
                totalPages,
                size,
                number
        );
    }
}