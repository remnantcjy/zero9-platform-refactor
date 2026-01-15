package com.zero9platform.domain.post;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.post.model.request.PostCreateRequest;
import com.zero9platform.domain.post.model.response.PostCreateResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CommonResponse<PostCreateResponse>> postCreateHandle(@RequestBody PostCreateRequest request) {

        // 인증/인가 구현 시 수정예정
        Long userId = 1L;

        PostCreateResponse response = postService.postCreate(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반게시물 생성 성공", response));
    }
}
