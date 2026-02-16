package com.zero9platform.domain.post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.PostType;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.post.entity.Post;
import com.zero9platform.domain.post.model.request.PostCreateRequest;
import com.zero9platform.domain.post.model.request.PostUpdateRequest;
import com.zero9platform.domain.post.model.response.*;
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
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 공지 및 문의사항 생성
     */
    @Transactional
    public PostCreateResponse postCreate(Long userId, PostCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (request.getPostType() == PostType.NOTICE && !UserRole.ADMIN.name().equals(user.getRole())) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        Post saved = postRepository.save(new Post(user, request.getPostType().name(), request.getTitle(), request.getContent()));

        return PostCreateResponse.from(saved);
    }

    /**
     * 공지 / 문의 상세조회
     */
    @Transactional(readOnly = true)
    public PostGetDetailResponse postGetDetail(Long id) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));
        return PostGetDetailResponse.from(post);
    }

    /**
     * 유형별 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<PostGetListResponse> postGetPage(PostType postType, Pageable pageable) {

        return postRepository.findAllByTypeAndDeletedAtIsNull(postType.name(), pageable)
                .map(PostGetListResponse::from);
    }

    /**
     * 공지 및 문의 수정
     */
    @Transactional
    public PostUpdateResponse postUpdate(Long userId, Long id, PostUpdateRequest request) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        // 공지사항인 경우: 관리자 권한 확인
        if (PostType.NOTICE.name().equals(post.getType())) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
            if (!UserRole.ADMIN.name().equals(user.getRole())) {
                throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
            }
        }
        // 문의사항인 경우: 작성자 본인 확인
        else {
            validOwner(post, userId);
        }

        post.update(request.getTitle(), request.getContent());
        return PostUpdateResponse.from(post);
    }

    /**
     * 공지 및 문의 삭제
     */
    @Transactional
    public void postDelete(AuthUser authUser, Long id) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        validOwnerOrAdmin(post, authUser.getId(), authUser.getUserRole());

        post.delete();
    }

    /**
     * 게시물 작성자 본인 여부 검증
     */
    private void validOwner(Post post, Long userId) {

        Long ownerId = post.getUser().getId();

        if (!ownerId.equals(userId)) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }
    }

    /**
     * 게시물 작성자 본인 여부 or 관리자 여부 검증
     */
    private void validOwnerOrAdmin(Post post, Long userId, UserRole userRole) {

        Long ownerId = post.getUser().getId();

        if (!ownerId.equals(userId) && userRole != UserRole.ADMIN) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }
    }
}