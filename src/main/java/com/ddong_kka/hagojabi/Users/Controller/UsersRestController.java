package com.ddong_kka.hagojabi.Users.Controller;

import com.ddong_kka.hagojabi.Exception.EmailDuplicateException;
import com.ddong_kka.hagojabi.Exception.InvalidPasswordException;
import com.ddong_kka.hagojabi.Exception.UserNotFoundException;
import com.ddong_kka.hagojabi.Users.DTO.UserDetailDTO;
import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;
import com.ddong_kka.hagojabi.Users.Service.UsersServiceImpl;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UsersRestController {

    private final UsersServiceImpl usersServiceImpl;

    public UsersRestController(UsersServiceImpl usersServiceImpl) {
        this.usersServiceImpl = usersServiceImpl;
    }

    @PostMapping("/join")
    public ResponseEntity<?> registerUser(@RequestBody UsersDTO usersDto){
        try{
            //회원가입 성공
            usersServiceImpl.register(usersDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입 성공"));
        } catch (EmailDuplicateException e){
            // 기입하려는 이메일이 중복된 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message",e.getMessage()));
        } catch (InvalidPasswordException e){
            // 요청 데이터가 잘못된 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // 예상치 못한 서버 에러
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원가입에 실패했습니다. 나중에 다시 시도해주세요."));
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo()  {
        try{
            return ResponseEntity.ok().body(usersServiceImpl.getUserInfo());
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of( "message" , e.getMessage(),"error","User Not Found"));
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of( "message", e.getMessage(),"error", "Illegal State"));
        } catch (Exception e){
            return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR))
                    .body(Map.of( "message", "회원 정보를 가져오는데 실패했습니다. 나중에 다시 시도해주세요.", "error", "Failed to fetch user info"));
        }
    }

    @PostMapping("/userProfile/edit")
    public  ResponseEntity<?> userNameUpdate(@RequestBody UserDetailDTO userDetailDTO){
        try{

            UserDetailDTO updateUser = usersServiceImpl.userNameUpdate(userDetailDTO);

            return ResponseEntity.ok().body(updateUser);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message",e.getMessage(),"error","Bad Request"));
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage(),"error", "User Not Found"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원명 수정에 실패했습니다. 나중에 다시 시도해주세요", "error", "Internal Server Error"));
        }

    }

    @PostMapping("/userProfile/passwordChange")
    public ResponseEntity<?> passwordChange(@RequestBody UserDetailDTO userDetailDTO){
       try {
            // 비밀번호 변경 서비스 호출 (예: usersService.passwordChange(userDetailDTO))
            usersServiceImpl.passwordChange(userDetailDTO);

            // 성공적인 경우 200 OK와 메시지를 반환
            return ResponseEntity.ok(Map.of("message","비밀번호 변경 성공"));
       } catch (IllegalArgumentException e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(Map.of( "message",e.getMessage(),"error", "Bad Request"));
       } catch (InvalidPasswordException e){
           // 요청 데이터가 잘못된 경우
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(Map.of("message", e.getMessage()));
       } catch (UserNotFoundException e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(Map.of("message", e.getMessage(),"error", "User Not Found"));
       } catch (Exception e) {
            // 예외 발생 시 적절한 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비밀번호 변경에 실패했습니다. 나중에 다시 시도해주세요.");
       }
    }

}
