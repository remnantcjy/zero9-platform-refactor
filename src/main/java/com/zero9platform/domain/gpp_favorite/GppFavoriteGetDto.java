package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GppFavoriteGetDto {

    private final Long gppId;
    private final Long userId;
    private final String productName;
    private final LocalDateTime createdAt;

    public static GppFavoriteGetDto from(GppFavorite gppFavorite) {
        return new GppFavoriteGetDto(
                gppFavorite.getId(),
                gppFavorite.getUser().getId(),
                gppFavorite.getGroupPurchasePost().getProductName(),
                gppFavorite.getGroupPurchasePost().getCreatedAt());
    }
}
