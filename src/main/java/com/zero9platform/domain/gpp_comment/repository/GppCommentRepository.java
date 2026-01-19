package com.zero9platform.domain.gpp_comment.repository;

import com.zero9platform.domain.gpp_comment.entity.GppComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GppCommentRepository extends JpaRepository<GppComment, Long> {

    Page<GppComment> findAllByGroupPurchasePost_Id(Long id, Pageable pageable);
}
