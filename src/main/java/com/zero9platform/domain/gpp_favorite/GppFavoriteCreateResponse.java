package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GppFavoriteCreateResponse {

    private final Long userId;
    private final String nickname;
    private final Long gppId;

    public static GppFavoriteCreateResponse from(GppFavorite gppFavorite) {
        return new GppFavoriteCreateResponse(
                gppFavorite.getUser().getId(),
                gppFavorite.getUser().getNickname(),
                gppFavorite.getGroupPurchasePost().getId()
        );
    }
}
