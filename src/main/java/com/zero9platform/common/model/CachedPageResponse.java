package com.zero9platform.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CachedPageResponse<T> {

    private List<T> content;    // 실제 데이터 리스트
    private int pageNumber;     // 현재 페이지 번호
    private int pageSize;       // 페이지 크기
    private long totalElements; // 전체 데이터 수
    private int totalPages;     // 전체 페이지 수

    public static <T> CachedPageResponse<T> of(Page<T> page) {
        return new CachedPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
