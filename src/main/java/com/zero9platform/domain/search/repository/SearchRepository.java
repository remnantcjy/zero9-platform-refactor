package com.zero9platform.domain.search.repository;

import com.zero9platform.domain.search.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchRepository extends JpaRepository<Search, Long> {

    Optional<Search> findByKeyword(String keyword);
}
