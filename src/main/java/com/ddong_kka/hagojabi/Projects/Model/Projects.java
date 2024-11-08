package com.ddong_kka.hagojabi.Projects.Model;

import com.ddong_kka.hagojabi.Users.Model.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    private LocalDateTime create_at;

    @CreationTimestamp
    private LocalDateTime update_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")  // Foreign key column
    private Users user;

    // New fields
    private String position;  // 포지션 예: "프론트엔드", "백엔드"

    private Integer teamSize; // 인원 예: 5

    private Integer durationInWeeks;  // 기간 (주 단위) 예: 12

    private String projectMode; // 진행 방식 예: "온라인", "오프라인", "혼합"

    private LocalDate recruitmentDeadline; // 모집 종료일

    @ElementCollection
    private List<String> techStack; // 기술 스택 예: ["Java", "Spring", "React"]

    @Builder
    public Projects(String title, String description, LocalDateTime create_at, LocalDateTime update_at, Users user,
                    String position, Integer teamSize, Integer durationInWeeks, String projectMode,
                    LocalDate recruitmentDeadline, List<String> techStack) {
        this.title = title;
        this.description = description;
        this.create_at = create_at;
        this.update_at = update_at;
        this.user = user;
        this.position = position;
        this.teamSize = teamSize;
        this.durationInWeeks = durationInWeeks;
        this.projectMode = projectMode;
        this.recruitmentDeadline = recruitmentDeadline;
        this.techStack = techStack;
    }
}
