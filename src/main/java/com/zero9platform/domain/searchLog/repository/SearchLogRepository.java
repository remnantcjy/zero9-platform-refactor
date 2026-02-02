package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.ranking.model.response.SearchLogRankingAggregateResponse;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.SearchLogListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    // 키워드 저장
    Optional<SearchLog> findByKeyword(String keyword);

    // 인기검색어 차트(상품명)
    @Query("""
                SELECT new com.zero9platform.domain.ranking.model.response.SearchLogRankingAggregateResponse(
                    s.keyword,
                    COUNT(s)
                )
                FROM SearchLog s
                WHERE s.createdAt BETWEEN :from AND :to
                GROUP BY s.keyword
                ORDER BY COUNT(s) DESC
            """)
    List<SearchLogRankingAggregateResponse> findTopKeywordsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

}
