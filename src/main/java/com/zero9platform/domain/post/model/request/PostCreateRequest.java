package com.zero9platform.domain.post.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    private String title;

    private String content;

    private String image;
}
