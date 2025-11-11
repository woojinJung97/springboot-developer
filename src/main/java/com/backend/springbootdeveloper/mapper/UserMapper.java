package com.backend.springbootdeveloper.mapper;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.domain.User;
import com.backend.springbootdeveloper.dto.AddUserRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {
    
    void insertUser(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    void updateMyHome(@Param("user")CustomUserDetails userDetails, @Param("request") AddUserRequest request);
}
