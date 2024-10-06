package com.ddong_kka.hagojabi.Users.Service;

import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder encoder;

    public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder encoder) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
    }

    public boolean existsByEmail(String email){
        return usersRepository.existsByEmail(email);
    }

    public void register(UsersDTO usersDTO){

        String encPassword = encoder.encode(usersDTO.getPassword());

        Users users = Users.builder()
                .email(usersDTO.getEmail())
                .username(usersDTO.getUsername())
                .password(encPassword)
                .role("ROLE_USER")
                .build();

        usersRepository.save(users);

    }
}
