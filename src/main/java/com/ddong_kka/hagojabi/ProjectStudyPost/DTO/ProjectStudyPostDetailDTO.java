package com.ddong_kka.hagojabi.ProjectStudyPost.DTO;

import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectStudyPostDetailDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<String> position;
    private String peopleCount;
    private String duration;
    private String projectMode;
    private LocalDate recruitmentDeadline;
    private List<String> techStack;
    private String recruitmentType;
    private String contactEmail;
    private int viewCount;
    private String authorName; // 작성자 이름 (Users 엔티티에서 가져올 수 있음)

    public ProjectStudyPostDetailDTO(ProjectStudyPost entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.createAt = entity.getCreate_at();
        this.updateAt = entity.getUpdate_at();
        this.position = entity.getPosition();
        this.peopleCount = entity.getPeopleCount();
        this.duration = entity.getDuration();
        this.projectMode = entity.getProjectMode();
        this.recruitmentDeadline = entity.getRecruitmentDeadline();
        this.techStack = entity.getTechStack();
        this.recruitmentType = entity.getRecruitmentType();
        this.contactEmail = entity.getContactEmail();
        this.viewCount = entity.getViewCount();

        // Users 엔티티에서 필요한 정보만 가져옴
        if (entity.getUser() != null) {
            this.authorName = entity.getUser().getUsername(); // Users 엔티티에 `name` 필드가 있다고 가정
        }
    }
}
