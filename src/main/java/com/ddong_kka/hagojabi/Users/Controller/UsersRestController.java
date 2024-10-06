package com.ddong_kka.hagojabi.Users.Controller;

import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;
import com.ddong_kka.hagojabi.Users.Service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersRestController {

    private final UsersService usersService;

    public UsersRestController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> registerUser(@RequestBody UsersDTO usersDto){

        System.out.println("실행확인 ");
        // 중복된 이메일이 있는지 검사
        if (usersService.existsByEmail(usersDto.getEmail())) {
            // 409 Conflict 응답 반환
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다.");
        }

        usersService.register(usersDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!");
    }
}
