package com.ddong_kka.hagojabi.Users.Interface;

import com.ddong_kka.hagojabi.Users.DTO.UserDetailDTO;
import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;

public interface UsersService {
    void register(UsersDTO usersDTO);
    void passwordChange(UserDetailDTO userDetailDTO);
    UserDetailDTO userNameUpdate(UserDetailDTO userDetailDTO);
    UserDetailDTO getUserInfo();
    boolean existsByEmail(String email);
}
