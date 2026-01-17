package com.zero9platform.common.config;

import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.jwt.JwtAccessDeniedHandler;
import com.zero9platform.common.jwt.JwtAuthenticationEntryPoint;
import com.zero9platform.common.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .exceptionHandling(ex -> ex.accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/zero9/auth/**").permitAll()
                        .requestMatchers("/zero9/{gppId}/favorites/**").permitAll()
                        .requestMatchers("/zero9/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/zero9/users").permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/zero9/users",
                                "/zero9/influencers"
                        ).hasRole(UserRole.ADMIN.name())
                        .requestMatchers("/zero9/admin/**").hasRole(UserRole.ADMIN.name())
                        .anyRequest().authenticated()   // 인가 - 위의 도메인 주소가 아니면 통과 x (출입 권한이 있는지 확인, 체크)
                )
                .build();
    }
}
