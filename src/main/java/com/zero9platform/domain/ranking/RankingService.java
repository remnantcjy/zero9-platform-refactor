package com.zero9platform.domain.ranking;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;

    public List<GroupPurchasePostRankingResponse> gppRanking() {

        List<GroupPurchasePost> posts =
                groupPurchasePostRepository.findTop10ByDeletedAtIsNullOrderByViewCountDesc();

        return IntStream.range(0, posts.size())
                .mapToObj(i ->
                        GroupPurchasePostRankingResponse.from(
                                i + 1,
                                posts.get(i)
                        )
                )
                .toList();
    }
}
