package com.zero9platform.domain.user.model.user.response;

import com.zero9platform.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetailResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String phone;

    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone()
        );
    }
}
