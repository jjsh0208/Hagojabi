package com.ddong_kka.hagojabi.ProjectStudyPost.Configuration;

import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class ProjectStudyPostScheduler {

    private final ProjectStudyPostRepository projectStudyPostRepository;

    public ProjectStudyPostScheduler(ProjectStudyPostRepository projectStudyPostRepository) {
        this.projectStudyPostRepository = projectStudyPostRepository;
    }


    /**
     * 매일 자정 00:00에 오늘 날짜를 지난 게시글은 비활성화 변경
     * **/
    @Scheduled(cron = "0 0 0 * * *")
    public void updateActiveStatusForExpiredPosts() {
        LocalDate today = LocalDate.now();

        List<ProjectStudyPost> posts = projectStudyPostRepository.findByRecruitmentDeadlineBeforeAndActiveTrue(today);

        for(ProjectStudyPost post : posts){
            post.setActive(false);
        }

        projectStudyPostRepository.saveAll(posts);
    }





}
