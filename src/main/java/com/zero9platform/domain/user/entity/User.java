package com.zero9platform.domain.user.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private String influencerSocialLink;

    @Column
    private LocalDateTime deletedAt;


    public User(
            String loginId,
            String password,
            String email,
            String name,
            UserRole role,
            String phone,
            String nickname
    ) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.nickname = nickname;
    }

}
