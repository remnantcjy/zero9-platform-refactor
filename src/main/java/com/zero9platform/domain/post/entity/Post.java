package com.zero9platform.domain.post.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String image;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column
    private LocalDateTime deletedAt;

    public Post(User user, String title, String content, String image) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = 0L;
        this.deletedAt = null;
    }

    public void increaseViewCount() {
        this.viewCount = this.viewCount + 1L;
    }
}
