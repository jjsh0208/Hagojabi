package com.ddong_kka.hagojabi.Users.Controller;

import com.ddong_kka.hagojabi.Users.DTO.EmailRequest;
import com.ddong_kka.hagojabi.Users.Service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class EmailRestController {

    private final EmailService emailService;

    public EmailRestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/emailSend")
    public ResponseEntity<?> emailSend(@RequestBody EmailRequest emailRequest){

        try{
            emailService.sendMail(emailRequest.getEmail());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",e.getMessage()));
        }
    }

    // 인증번호 일치여부 확인
    @PostMapping("/emailVerify")
    public ResponseEntity<?> EmailVerify(@RequestBody EmailRequest emailRequest) {

        boolean isMatch = emailService.verifyCode(emailRequest.getCode());

        return ResponseEntity.ok(Map.of("result", isMatch));
    }
}
