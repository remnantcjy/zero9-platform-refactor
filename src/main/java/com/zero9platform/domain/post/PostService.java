package com.zero9platform.domain.post;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.post.entity.Post;
import com.zero9platform.domain.post.model.request.PostCreateRequest;
import com.zero9platform.domain.post.model.response.PostCreateResponse;
import com.zero9platform.domain.post.repository.PostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER));

        //Post post = new Post(user, request.getTitle(), request.getContent(), request.getImage());

        //Post savedPost = postRepository.save(post);

        Post savedPost = postRepository.save(new Post(user, request.getTitle(), request.getContent(), request.getImage()));

        return PostCreateResponse.from(savedPost);

    }
}
