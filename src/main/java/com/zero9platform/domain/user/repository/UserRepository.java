package com.zero9platform.domain.user.repository;

import com.zero9platform.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
