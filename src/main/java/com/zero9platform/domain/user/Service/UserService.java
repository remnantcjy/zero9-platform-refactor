package com.zero9platform.domain.user.Service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.jwt.JwtUtil;
import com.zero9platform.domain.influencer.entity.Influencer;
import com.zero9platform.domain.influencer.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.model.user.request.UserCreateRequest;
import com.zero9platform.domain.user.model.user.request.UserDeleteRequest;
import com.zero9platform.domain.user.model.user.request.UserUpdateRequest;
import com.zero9platform.domain.user.model.user.response.UserCreateResponse;
import com.zero9platform.domain.user.model.user.response.UserDetailResponse;
import com.zero9platform.domain.user.model.user.response.UserUpdateResponse;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InfluencerRepository influencerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request) {

        // 중복되는 아이디 조회
        checkDuplicate(userRepository.existsByLoginId(request.getLoginId()), ExceptionCode.LOGINID_EXIST);

        // 중복되는 이메일 조회
        checkDuplicate(userRepository.existsByEmail(request.getEmail()), ExceptionCode.EMAIL_EXIST);

        // 중복되는 핸드폰번호 조회
        checkDuplicate(userRepository.existsByPhone(request.getPhone()), ExceptionCode.PHONE_EXIST);

        // 중복되는 이메일 조회
        checkDuplicate(userRepository.existsByNickname(request.getNickname()), ExceptionCode.NICKNAME_EXIST);

        User user = new User(request.getLoginId(), passwordEncoder.encode(request.getPassword()), request.getEmail(), request.getName(), request.getRole().name(), request.getPhone(), request.getNickname());

        User userCreated = userRepository.save(user);

        String token = "";

        if (request.getRole() == UserRole.INFLUENCER) {
            influencerRepository.save(new Influencer(userCreated));
        } else {
            // 토큰 생성
            token = jwtUtil.createToken(userCreated.getId(), userCreated.getNickname(), UserRole.valueOf(user.getRole()));
        }

        return UserCreateResponse.from(userCreated, token);
    }

    /**
     * 사용자 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserDetailResponse userDetail(Long userId, boolean isAdmin) {

        User user = findById(userId);

        // 관리자 프로필 보호 조건
        if (!isAdmin) {
            if (UserRole.valueOf(user.getRole()) == UserRole.ADMIN) {
                throw new CustomException(ExceptionCode.NO_PERMISSION);
            }
        }

        return UserDetailResponse.from(user);
    }

    /**
     * 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserDetailResponse> userList() {

        List<User> userList = userRepository.findAll();

        return userList.stream()
                .map(UserDetailResponse::from)
                .toList();
    }

    /**
     * 사용자 프로필 수정
     */
    @Transactional
    public UserUpdateResponse userUpdate(Long userId, UserUpdateRequest request) {

        User user = findById(userId);

        // 나를 제외한 중복되는 이메일 조회
        checkDuplicate(userRepository.existsByEmailAndIdNot(request.getEmail(), userId), ExceptionCode.EMAIL_EXIST);

        // 나를 제외한 중복되는 닉네임 조회
        checkDuplicate(userRepository.existsByNicknameAndIdNot(request.getNickname(), userId), ExceptionCode.NICKNAME_EXIST);

        // 나를 제외한 중복되는 핸드폰번호 조회
        checkDuplicate(userRepository.existsByPhoneAndIdNot(request.getPhone(), userId), ExceptionCode.PHONE_EXIST);

        user.userUpdate(request.getEmail(), request.getNickname(), request.getPhone());

        return UserUpdateResponse.from(user);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void userDelete(Long userId, UserDeleteRequest request) {

        User user = findById(userId);

        boolean passwordMatches = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_MATCH);
        }

        user.userDelete();
    }

    /**
     * 공통 검증 메서드
     */
    private void checkDuplicate(boolean exists, ExceptionCode code) {

        if (exists) {
            throw new CustomException(code);
        }
    }

    /**
     * 회원 조회 메서드
     */
    private User findById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return user;
    }
}
