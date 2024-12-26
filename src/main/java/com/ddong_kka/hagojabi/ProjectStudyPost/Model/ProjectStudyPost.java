package com.ddong_kka.hagojabi.ProjectStudyPost.Model;

import com.ddong_kka.hagojabi.Users.Model.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class ProjectStudyPost {

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

    @ElementCollection
    @CollectionTable(name = "positions", joinColumns = @JoinColumn(name = "post_id"))
    private List<String> position; // 예: ["프론트엔드", "백엔드"]

    private String peopleCount; // 인원 예: 5

    private String duration;  // 기간 (주 단위) 예: 12

    private String projectMode; // 진행 방식 예: "온라인"

    @DateTimeFormat(pattern = "yyyy-MM-dd")  // 예: 2024-11-15
    private LocalDate recruitmentDeadline;

    @ElementCollection
    @CollectionTable(name = "tech_stacks", joinColumns = @JoinColumn(name = "post_id"))
    private List<String> techStack; // 예: ["Java", "Spring", "React"]

    private String recruitmentType;

    private String contactEmail; // 연락처 이메일

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    private boolean active;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")  // Foreign key column
    @JsonIgnore
    private Users user;

    @Builder
    public ProjectStudyPost(String title, String description, LocalDateTime create_at, LocalDateTime update_at, Users user,
                            List<String> position, String peopleCount, String duration, String projectMode,
                            LocalDate recruitmentDeadline, List<String> techStack, String contactEmail, String recruitmentType) {
        this.title = title;
        this.description = description;
        this.create_at = create_at;
        this.update_at = update_at;
        this.user = user;
        this.position = position;
        this.peopleCount = peopleCount;
        this.duration = duration;
        this.projectMode = projectMode;
        this.recruitmentDeadline = recruitmentDeadline;
        this.techStack = techStack;
        this.recruitmentType = recruitmentType;
        this.contactEmail = contactEmail;
        this.active = true;
        this.viewCount = 0;
    }
}
