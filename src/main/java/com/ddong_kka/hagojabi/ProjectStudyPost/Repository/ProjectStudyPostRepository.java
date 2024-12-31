package com.ddong_kka.hagojabi.ProjectStudyPost.Repository;


import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProjectStudyPostRepository extends JpaRepository<ProjectStudyPost, Long> {

    Page<ProjectStudyPost> findAllByActiveTrue(Pageable pageable);

    // 모집 마감일 특정 날자 이전이며 active 가 true 인 게시글 데이터를 검색
    List<ProjectStudyPost> findByRecruitmentDeadlineBeforeAndActiveTrue(LocalDate recruitmentDeadline);
}
