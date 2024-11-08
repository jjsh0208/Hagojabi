package com.ddong_kka.hagojabi.Users.Repository;

import com.ddong_kka.hagojabi.Users.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    //findBy
    Boolean existsByEmail(String email); // 이메일 존재 여부 확인

    Boolean existsByUsername(String username); // 사용자명 존재 여부 확인

    // Retrieve user by username wrapped in Optional
    Optional<Users> findByUsername(String username);

    // Retrieve user by email
    Optional<Users> findByEmail(String email);
}
