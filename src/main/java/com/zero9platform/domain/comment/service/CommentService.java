package com.zero9platform.domain.comment.service;


import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.comment.entity.Comment;
import com.zero9platform.domain.comment.model.request.CommentCreateRequest;
import com.zero9platform.domain.comment.model.request.CommentGetListRequest;
import com.zero9platform.domain.comment.model.request.CommentUpdateRequest;
import com.zero9platform.domain.comment.model.response.CommentCreateResponse;
import com.zero9platform.domain.comment.model.response.CommentGetListResponse;
import com.zero9platform.domain.comment.repository.CommentRepository;
import com.zero9platform.domain.post.entity.Post;
import com.zero9platform.domain.post.repository.PostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentCreateResponse commentCreate(AuthUser authUser, CommentCreateRequest request) {

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedAtIsNull(request.getPostId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        Comment saved = commentRepository.save(new Comment(post, user, request.getContent()));

        return CommentCreateResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentGetListResponse> commentGetList(CommentGetListRequest request, Pageable pageable) {

        postRepository.findByIdAndDeletedAtIsNull(request.getPostId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        Page<CommentGetListResponse> page = commentRepository.findAllByPostId(request.getPostId(), pageable)
                        .map(CommentGetListResponse::from);

        return PageResponse.from(page);
    }

    @Transactional
    public void commentUpdate(AuthUser authUser, Long id, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_COMMENT));

        validOwner(comment, authUser.getId());

        comment.update(request.getContent());
    }


    private void validOwner(Comment comment, Long userId) {

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }
}
