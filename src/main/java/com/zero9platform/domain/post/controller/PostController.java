package com.zero9platform.domain.post.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.post.model.response.PostGetListResponse;
import com.zero9platform.domain.post.service.PostService;
import com.zero9platform.domain.post.model.request.PostCreateRequest;
import com.zero9platform.domain.post.model.request.PostUpdateRequest;
import com.zero9platform.domain.post.model.response.PostCreateResponse;
import com.zero9platform.domain.post.model.response.PostGetDetailResponse;
import com.zero9platform.domain.post.model.response.PostUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CommonResponse<PostCreateResponse>> postCreateHandler(@AuthenticationPrincipal AuthUser authUser, @RequestBody PostCreateRequest request) {

        PostCreateResponse response = postService.postCreate(authUser, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("일반게시물 생성 성공", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PostGetDetailResponse>> postGetDetailHandler(@PathVariable Long id) {

        PostGetDetailResponse response = postService.postGetDetail(id);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 상세조회 성공", response));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PostGetListResponse>>> postGetListHandler(Pageable pageable){

        PageResponse<PostGetListResponse> response = postService.postGetList(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 목록조회 성공", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<PostUpdateResponse>> postUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long id, @RequestBody PostUpdateRequest request) {

        PostUpdateResponse response = postService.postUpdate(authUser, id, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 수정 성공", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> postDeleteHandler( @AuthenticationPrincipal AuthUser authUser, @PathVariable Long id) {

        postService.postDelete(authUser, id);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 삭제 성공", null));
    }
}
