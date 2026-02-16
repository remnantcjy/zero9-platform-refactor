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

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


    @Column
    private LocalDateTime deletedAt;

    public Post(User user, String type,String title, String content) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
        this.deletedAt = null;
    }

    public void update(String title, String content) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}