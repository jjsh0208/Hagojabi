package com.ddong_kka.hagojabi.Users.Repository;

import com.ddong_kka.hagojabi.Users.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

    //findBy
    Boolean existsByEmail(String email); // 이메일 존재 여부 확인

    Boolean existsByUsername(String username); // 사용자명 존재 여부 확인
    
    Users findByUsername(String username); // 사용자명으로 사용자 정보 가져옴

    Users findByEmail(String email); // 이메일로 사용자 정보 가져옴
}
