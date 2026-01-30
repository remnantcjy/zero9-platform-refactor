//package com.zero9platform.rankingDummy;
//import com.zero9platform.domain.user.entity.User;
//import com.zero9platform.domain.user.repository.UserRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//@Profile("local")
//@Component
//@RequiredArgsConstructor
//public class UserDummyInitializer {
//
//    private final UserRepository userRepository;
//
//    @PostConstruct
//    public void init() {
//
//        // 이미 유저 있으면 생성 안 함
//        if (userRepository.count() > 0) return;
//
//        User user = new User(
//                "test_user",          // loginId
//                "password1234",       // password (로컬용)
//                "test@test.com",      // email
//                "테스트유저",         // name
//                "USER",               // role
//                "01012345678",        // phone
//                "테스트닉네임"        // nickname
//        );
//
//        userRepository.save(user);
//    }
//}