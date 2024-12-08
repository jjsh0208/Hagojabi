package com.ddong_kka.hagojabi.Users.DTO;


import com.ddong_kka.hagojabi.Users.Model.Users;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailDTO {

    private String username;
    private String email;
    private String provider;
    private String password;

    public UserDetailDTO(Users users) {
        this.username = users.getUsername();
        this.email = users.getEmail();
        this.provider = users.getProvider();
        this.password = users.getPassword();
    }
}
