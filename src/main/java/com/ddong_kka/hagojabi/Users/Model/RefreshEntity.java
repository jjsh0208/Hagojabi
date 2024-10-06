package com.ddong_kka.hagojabi.Users.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;   // 유저 이메일
    private String refresh;     // 유저가 가지고있는 refresh 토큰
    private String expiration;  // refresh 토큰이 만료되는 시간

}
