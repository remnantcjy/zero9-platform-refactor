package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import com.zero9platform.domain.gpp_favorite.repository.GppFavoriteRepository;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;;

import java.beans.Transient;

@Service
@RequiredArgsConstructor
public class GppFavoriteService {

    private final GppFavoriteRepository gppFavoriteRepository;
    private final UserRepository userRepository;
    private final GroupPurchasePostRepository groupPurchasePost;

    @Transient
    public void favoriteCreate(Long gppId, Long userId) {

        //유저 아이디정보 DB에서 추출
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER));

        //게시물 존재 여부 확인
        GroupPurchasePost post = groupPurchasePost.findById(gppId).orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        //중복 등록 방지
        if (gppFavoriteRepository.existsByUserAndGroupPurchasePost(user, post)) {
            throw new CustomException(ExceptionCode.ALREADY_FAVORITE);
        }

        //게시물이 있으면 로그인 되있는 유저 아이디를 추가
        GppFavorite gppFavorite = new GppFavorite(user, post);
        gppFavoriteRepository.save(gppFavorite);
    }
}
