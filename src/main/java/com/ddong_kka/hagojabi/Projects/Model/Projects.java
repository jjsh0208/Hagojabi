package com.ddong_kka.hagojabi.Projects.Model;

import com.ddong_kka.hagojabi.Users.Model.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @CreationTimestamp
    private LocalDateTime create_at;

    @CreationTimestamp
    private LocalDateTime update_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // This specifies the foreign key column
    private Users user;

    @Builder
    public Projects(String title, String description, LocalDateTime create_at, LocalDateTime update_at, Users user) {
        this.title = title;
        this.description = description;
        this.create_at = create_at;
        this.update_at = update_at;
        this.user = user;
    }
}
