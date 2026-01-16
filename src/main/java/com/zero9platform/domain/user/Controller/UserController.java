package com.zero9platform.domain.user.Controller;

import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.user.Service.UserService;
import com.zero9platform.domain.user.model.user.request.UserCreateRequest;
import com.zero9platform.domain.user.model.user.request.UserDeleteRequest;
import com.zero9platform.domain.user.model.user.request.UserUpdateRequest;
import com.zero9platform.domain.user.model.user.response.UserCreateResponse;
import com.zero9platform.domain.user.model.user.response.UserDetailResponse;
import com.zero9platform.domain.user.model.user.response.UserUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/users")
    public ResponseEntity<CommonResponse<UserCreateResponse>> createUserHandler(@Valid @RequestBody UserCreateRequest request) {

        UserCreateResponse response = userService.createUser(request);

        String userTypeMessage = request.getRole().equals(UserRole.USER) ? "회원가입 성공" : "인플루언서는 관리자 승인이후 활동 가능합니다.";

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(userTypeMessage, response));
    }

    /**
     * 사용자 프로필 조회
     */
    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<CommonResponse<UserDetailResponse>> userDetailHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long userId) {

        // 권한 확인
        boolean isAdmin = false;

        if (authUser.getUserRole() == UserRole.ADMIN) {
            isAdmin = true;
        }

        UserDetailResponse response = userService.userDetail(userId, isAdmin);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("사용자 프로필 조회 성공", response));
    }

    /**
     * 사용자 목록 조회
     */
    @GetMapping("/users")
    public ResponseEntity<CommonResponse<List<UserDetailResponse>>> userListHandler() {

        List<UserDetailResponse> response = userService.userList();

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("사용자 목록 조회 성공", response));
    }

    /**
     * 사용자 프로필 수정
     */
    @PutMapping("/users")
    public ResponseEntity<CommonResponse<UserUpdateResponse>> userUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @Valid  @RequestBody UserUpdateRequest request) {

        UserUpdateResponse response = userService.userUpdate(authUser.getId(), request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("사용자 프로필 수정이 완료 되었습니다.", response));
    }

    @DeleteMapping("/users")
    public ResponseEntity<CommonResponse<Void>> userDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserDeleteRequest request) {

        userService.userDelete(authUser.getId(), request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("회원탈퇴가 완료 되었습니다.",null));
    }

}
