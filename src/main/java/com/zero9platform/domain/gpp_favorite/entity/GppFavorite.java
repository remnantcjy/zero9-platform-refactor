package com.zero9platform.domain.gpp_favorite.entity;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "gpp_favorites")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GppFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpp_id", nullable = false)
    private GroupPurchasePost groupPurchasePost;

    //찜 등록용 생성자 주입
    public GppFavorite(User user, GroupPurchasePost groupPurchasePost) {
        this.user = user;
        this.groupPurchasePost = groupPurchasePost;
    }
}
