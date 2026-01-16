package com.zero9platform.domain.comment;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.comment.model.request.CommentCreateRequest;
import com.zero9platform.domain.comment.model.request.CommentGetListRequest;
import com.zero9platform.domain.comment.model.response.CommentCreateResponse;
import com.zero9platform.domain.comment.model.response.CommentGetListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommonResponse<CommentCreateResponse>> commentCreateHandler(@RequestBody CommentCreateRequest request) {

        // 인증/인가 구현 시 수정예정
        Long userId = 1L;

        CommentCreateResponse response = commentService.commentCreate(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("댓글 생성 성공", response));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<CommentGetListResponse>>> commentGetListHandler(@RequestBody CommentGetListRequest request, Pageable pageable) {

        PageResponse<CommentGetListResponse> response = commentService.commentGetList(request, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("댓글 조회 성공", response));
    }
}
