package com.ddong_kka.hagojabi.Users.Controller;

import com.ddong_kka.hagojabi.Exception.UserNotFoundException;
import com.ddong_kka.hagojabi.Users.DTO.UserDetailDTO;
import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;
import com.ddong_kka.hagojabi.Users.Service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UsersRestController {

    private final UsersService usersService;

    public UsersRestController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> registerUser(@RequestBody UsersDTO usersDto){
        try{
            // 중복된 이메일이 있는지 검사
            if (usersService.existsByEmail(usersDto.getEmail())) {
                // 409 Conflict 응답 반환
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","이미 사용 중인 이메일입니다."));
            }
            usersService.register(usersDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입 성공!"));
        }catch (IllegalArgumentException e) {
            // 요청 데이터가 잘못된 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "잘못된 요청 데이터입니다."));
        } catch (Exception e) {
            // 예상치 못한 서버 에러
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원가입에 실패했습니다. 나중에 다시 시도해주세요."));
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo()  {
        try{
            return ResponseEntity.ok().body(usersService.getUserInfo());
        }catch (Exception e){
            return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR))
                    .body(Map.of("error", "Failed to fetch user info", "message", e.getMessage()));
        }
    }

    @PostMapping("/userProfile/edit")
    public  ResponseEntity<?> userNameUpdate(@RequestBody UserDetailDTO userDetailDTO){
        try{

            UserDetailDTO updateUser = usersService.userNameUpdate(userDetailDTO);

            return ResponseEntity.ok().body(updateUser);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error","Bad Request", "message",e.getMessage()));
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User Not Found", "message", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred."));
        }

    }

    @PostMapping("/userProfile/passwordChange")
    public ResponseEntity<?> passwordChange(@RequestBody UserDetailDTO userDetailDTO){
       try {
            // 비밀번호 변경 서비스 호출 (예: usersService.passwordChange(userDetailDTO))
            usersService.passwordChange(userDetailDTO);

            // 성공적인 경우 200 OK와 메시지를 반환
            return ResponseEntity.ok("비밀번호 변경 성공");
       }
       catch (IllegalArgumentException e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(Map.of("error", "Bad Request", "message",e.getMessage()));
       }
       catch (UserNotFoundException e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(Map.of("error", "User Not Found", "message", e.getMessage()));
       }
       catch (Exception e) {
            // 예외 발생 시 적절한 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비밀번호 변경에 실패했습니다.");
       }
    }

}
