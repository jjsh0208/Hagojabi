package com.ddong_kka.hagojabi.Users.Model;

import com.ddong_kka.hagojabi.Projects.Model.Projects;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String username;

    private String password;

    private String role;

    private String provider; //oauth2 로그인 플랫폼

    private String providerId;

    @CreationTimestamp
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Projects> projects;  // A user can have multiple projects

    @Builder
    public Users(String email, String username, String password, String role, String provider, String providerId, LocalDateTime createDate) {

        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.createDate = createDate;
    }
}
