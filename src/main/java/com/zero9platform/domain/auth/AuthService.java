package com.zero9platform.domain.auth;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.jwt.JwtUtil;
import com.zero9platform.domain.auth.model.request.AuthLoginRequest;
import com.zero9platform.domain.auth.model.response.AuthLoginResponse;
import com.zero9platform.domain.admin.entity.Influencer;
import com.zero9platform.domain.admin.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final InfluencerRepository influencerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public AuthLoginResponse login(AuthLoginRequest request) {

        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 탈퇴한 회원 검사
        if (user.getDeletedAt() != null) {
            throw new CustomException(ExceptionCode.USER_WITHDRAWN);
        }

        // 비밀번호 검사
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_MATCH);
        }

        // role request enum 변환
        UserRole role = UserRole.valueOf(user.getRole());

        // 인플루언서 승인 확인
        if (role == UserRole.INFLUENCER) {
            boolean approved = influencerRepository.findByUserId(user.getId())
                    .map(Influencer::getInfluencerApprovalStatus)
                    .orElse(false);

            log.info("user status: {}", approved);

            if (!approved) {
                throw new CustomException(ExceptionCode.INFLUENCER_NOT_APPROVED);
            }
        }

        String token = jwtUtil.createToken(user.getId(), user.getNickname(), role);

        return new AuthLoginResponse(token);
    }
}
