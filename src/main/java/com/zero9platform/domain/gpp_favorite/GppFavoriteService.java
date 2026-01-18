package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import com.zero9platform.domain.gpp_favorite.model.response.GppFavoriteCreateResponse;
import com.zero9platform.domain.gpp_favorite.model.GppFavoriteGetDto;
import com.zero9platform.domain.gpp_favorite.repository.GppFavoriteRepository;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GppFavoriteService {

    private final GppFavoriteRepository gppFavoriteRepository;
    private final UserRepository userRepository;
    private final GroupPurchasePostRepository groupPurchasePost;

    /**
     * 찜 등록
     */
    @Transactional
    public GppFavoriteCreateResponse gppFavoriteCreate(Long gppId, Long userId) {

        //NPE 방어
        if (gppId == null || userId == null) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

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

        return GppFavoriteCreateResponse.from(gppFavorite);
    }

    /**
     * 찜 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<GppFavoriteGetDto> gppFavoritePage(Long userId, Pageable pageable) {

        //NPE 방어
        if (userId == null) {
            throw new CustomException(ExceptionCode.NOT_FOUND_USER);
        }

        //유저 아이디가 디비에서 favorite 한 리스트를 가져온다.
        Page<GppFavorite> gppFavoritePage = gppFavoriteRepository.findByUserId(userId, pageable);

        //없으면 예외처리
        if (gppFavoritePage.isEmpty()) {
            throw new CustomException(ExceptionCode.FAVORITE_NOT_FOUND);
        }

        //있으면 리스트를 리스폰스에 담는다.
        Page<GppFavoriteGetDto> GppFavoriteGetDtoPage =
                gppFavoritePage.map(GppFavoriteGetDto::from);

        return PageResponse.from(GppFavoriteGetDtoPage);
    }

    /**
     * 찜 등록 취소
     */
    @Transactional
    public void gppFavoriteCancellation(Long gppId, Long userId) {

        //NPE 방어
        if (gppId == null) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        //유저 아이디정보 DB에서 추출
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER));

        //게시물 존재 여부 확인
        GroupPurchasePost post = groupPurchasePost.findById(gppId).orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        // 찜 존재 여부 확인
        GppFavorite favorite = gppFavoriteRepository.findByUser_IdAndGroupPurchasePost_Id(userId, gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_FAVORITE));

        // 삭제
        gppFavoriteRepository.delete(favorite);
    }

}
