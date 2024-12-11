package com.ddong_kka.hagojabi.Users.Controller;

import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.Users.DTO.UserDetailDTO;
import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UsersRestController {

    private final UsersService usersService;

    public UsersRestController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> registerUser(@RequestBody UsersDTO usersDto){
        // 중복된 이메일이 있는지 검사
        if (usersService.existsByEmail(usersDto.getEmail())) {
            // 409 Conflict 응답 반환
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다.");
        }

        usersService.register(usersDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo()  {
        return ResponseEntity.ok().body(usersService.getUserInfo());
    }

    @PostMapping("/userProfile/edit")
    public  ResponseEntity<?> userNameUpdate(@RequestBody UserDetailDTO userDetailDTO){
        return ResponseEntity.ok().body(usersService.userNameUpdate(userDetailDTO));
    }

    @PostMapping("/userProfile/passwordChange")
    public ResponseEntity<?> passwordChange(@RequestBody UserDetailDTO userDetailDTO){
       try {
            // 비밀번호 변경 서비스 호출 (예: usersService.passwordChange(userDetailDTO))
            usersService.passwordChange(userDetailDTO);

            // 성공적인 경우 200 OK와 메시지를 반환
            return ResponseEntity.ok("비밀번호 변경 성공");
        } catch (Exception e) {
            // 예외 발생 시 적절한 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비밀번호 변경에 실패했습니다.");
        }
    }

}
