package com.ddong_kka.hagojabi.Exception;

public class InvalidPasswordException extends RuntimeException{
    // 기본 생성자
    public InvalidPasswordException() {
        super("비밀번호가 정책을 충족하지 않습니다.");
    }

    // 사용자 정의 메시지를 전달하는 생성자
    public InvalidPasswordException(String message) {
        super(message);
    }
}
