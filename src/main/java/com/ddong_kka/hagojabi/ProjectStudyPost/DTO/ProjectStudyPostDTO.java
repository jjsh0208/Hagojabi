package com.ddong_kka.hagojabi.ProjectStudyPost.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class ProjectStudyPostDTO {

    private String title;
    private String description;
    private List<String> position;  // 포지션 예: ["프론트엔드", "백엔드"]
    private String peopleCount;  // 인원 예: 5
    private String duration;  // 기간 (주 단위) 예: 12
    private String projectMode;  // 진행 방식 예: "온라인"
    private String recruitmentDeadline;  // 모집 종료일
    private List<String> techStack;  // 기술 스택 예: ["Java", "Spring", "React"]
    private String recruitmentType;
    private String contactEmail;
}