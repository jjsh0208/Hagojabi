package com.ddong_kka.hagojabi.ProjectStudyPost.Interface;

import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDetailDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectStudyPostService {
    Page<ProjectStudyPost> getPosts(Pageable pageable);
    ProjectStudyPostDetailDTO getDetail(Long id);
    Long register(ProjectStudyPostDTO projectStudyPostDTO);
    Long update(ProjectStudyPostDTO projectStudyPostDTO, Long id);
    void deletePost(Long id);
}
