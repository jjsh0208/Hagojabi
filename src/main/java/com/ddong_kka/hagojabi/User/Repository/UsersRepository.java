package com.ddong_kka.hagojabi.User.Repository;

import com.ddong_kka.hagojabi.User.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

    //findBy
    Users findByUsername(String username);
    Users findByEmail(String email);
}
