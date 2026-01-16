package com.zero9platform.domain.post.model.response;

import com.zero9platform.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageResponse {

    private final List<PostGetListResponse> content;
    private final long totalElements;
    private final int totalPages;
    private final int size;
    private final int number;

    public static PostPageResponse from(Page<PostGetListResponse> page) {
        return new PostPageResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}