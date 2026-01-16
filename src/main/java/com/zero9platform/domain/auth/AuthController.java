package com.zero9platform.domain.auth;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.request.AuthLoginRequest;
import com.zero9platform.domain.auth.model.response.AuthLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> loginHandler(@RequestBody AuthLoginRequest request) {

        AuthLoginResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("로그인 성공", response));
    }
}
