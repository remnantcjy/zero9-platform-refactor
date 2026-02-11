/*
package com.zero9platform;

import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User user = new User("admin", passwordEncoder.encode("12345678!!"), "admin@email.com", "관리자", "ADMIN", "010-0000-0000", "관리자");
        userRepository.save(user);

    }
}*/
