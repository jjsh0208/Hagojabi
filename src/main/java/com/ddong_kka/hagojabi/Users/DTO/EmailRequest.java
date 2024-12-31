package com.ddong_kka.hagojabi.Users.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {
    private String email;
    private String code;
}
