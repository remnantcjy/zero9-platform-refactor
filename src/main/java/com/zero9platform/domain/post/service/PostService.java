package com.zero9platform.domain.post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
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

    @Transactional
    public PostCreateResponse postCreate(Long userId, PostCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post post = new Post(user, request.getTitle(), request.getContent(), request.getImage());

        Post savedPost = postRepository.save(post);

        return PostCreateResponse.from(savedPost);
    }

    @Transactional(readOnly = true)
    public PostGetDetailResponse postGetDetail(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        post.increaseViewCount();

        return PostGetDetailResponse.from(post);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostGetListResponse> postGetList(Pageable pageable) {

        Page<PostGetListResponse> page = postRepository.findAllByDeletedAtIsNull(pageable)
                                                .map(PostGetListResponse::from);

        return  PageResponse.from(page);
    }

    @Transactional
    public PostUpdateResponse postUpdate(Long userId, Long id, PostUpdateRequest request) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        validOwnedBy(post, userId);

        post.update(request.getTitle(), request.getContent(), request.getImage());

        return PostUpdateResponse.from(post);

    }

    @Transactional
    public void postDelete(Long userId, Long id) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        validOwnedBy(post, userId);

        post.delete();
    }

    /**
     *  작성자 본인인지 검증
     */
    private void validOwnedBy(Post post, Long userId) {

        Long ownerId = post.getUser().getId();

        if(!ownerId.equals(userId)) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }
}
