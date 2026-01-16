package com.zero9platform.domain.post.repository;

import com.zero9platform.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);
}
