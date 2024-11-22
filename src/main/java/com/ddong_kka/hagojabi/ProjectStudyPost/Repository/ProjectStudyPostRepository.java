package com.ddong_kka.hagojabi.ProjectStudyPost.Repository;


import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectStudyPostRepository extends JpaRepository<ProjectStudyPost, Long> {

    Page<ProjectStudyPost> findAll(Pageable pageable);
}
