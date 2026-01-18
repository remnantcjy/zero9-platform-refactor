package com.zero9platform.domain.post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
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
     *  일반 게시물 생성
     */
    @Transactional
    public PostCreateResponse postCreate(Long userid, PostCreateRequest request) {

        User user = userRepository.findById(userid)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post saved = postRepository.save(new Post(user, request.getTitle(), request.getContent(), request.getImage()));

        return PostCreateResponse.from(saved);
    }

    /**
     *  일반 게시물 상세조회
     */
    @Transactional
    public PostGetDetailResponse postGetDetail(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        post.increaseViewCount();

        return PostGetDetailResponse.from(post);
    }

    /**
     *  일반 게시물 전체목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<PostGetListResponse> postGetList(Pageable pageable) {

        Page<PostGetListResponse> page = postRepository.findAllByDeletedAtIsNull(pageable)
                                                .map(PostGetListResponse::from);

        return  PageResponse.from(page);
    }

    /**
     *  일반 게시물 수정
     */
    @Transactional
    public PostUpdateResponse postUpdate(Long userId, Long id, PostUpdateRequest request) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        validOwner(post, userId);

        post.update(request.getTitle(), request.getContent(), request.getImage());

        return PostUpdateResponse.from(post);

    }

    /**
     *  일반 게시물 식제
     */
    @Transactional
    public void postDelete(Long userId, Long id) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        validOwner(post, userId);

        post.delete();
    }

    /**
     *  게시물 작성자 본인 여부 검증
     */
    private void validOwner(Post post, Long userId) {

        Long ownerId = post.getUser().getId();

        if(!ownerId.equals(userId)) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }
}
