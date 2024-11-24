package com.ddong_kka.hagojabi.ProjectStudyPost.Service;

import com.ddong_kka.hagojabi.Exception.DataNotFoundException;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDetailDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectStudyPostService {

    private final ProjectStudyPostRepository projectStudyPostRepository;
    private final UsersRepository usersRepository;

    public ProjectStudyPostService(ProjectStudyPostRepository projectStudyPostRepository, UsersRepository usersRepository) {
        this.projectStudyPostRepository = projectStudyPostRepository;
        this.usersRepository = usersRepository;
    }

    public Long register(ProjectStudyPostDTO projectStudyPostDTO) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        // Find the User by username in the database
        Users user = usersRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        ProjectStudyPost projects = ProjectStudyPost.builder()
                .title(projectStudyPostDTO.getTitle())
                .description(projectStudyPostDTO.getDescription())
                .position(projectStudyPostDTO.getPosition())
                .peopleCount(projectStudyPostDTO.getPeopleCount())
                .duration(projectStudyPostDTO.getDuration())
                .projectMode(projectStudyPostDTO.getProjectMode())
                .recruitmentDeadline(projectStudyPostDTO.getRecruitmentDeadline())
                .techStack(projectStudyPostDTO.getTechStack())
                .recruitmentType(projectStudyPostDTO.getRecruitmentType())
                .contactEmail(projectStudyPostDTO.getContactEmail())
                .user(user)
                .build();

       ProjectStudyPost projectStudyPost = projectStudyPostRepository.save(projects);

       return projectStudyPost.getId();
    }

    public ProjectStudyPostDetailDTO getDetail(Long id) {
        Optional<ProjectStudyPost> projectStudyPostOptional = projectStudyPostRepository.findById(id);

        // 하나의 게시물이 요청될 때 마다 조회수를 +1 증가
        if (projectStudyPostOptional.isPresent()){
            ProjectStudyPost projectStudyPost = projectStudyPostOptional.get();
            projectStudyPost.setViewCount(projectStudyPost.getViewCount() + 1);
            this.projectStudyPostRepository.save(projectStudyPost);
            return new ProjectStudyPostDetailDTO(projectStudyPostOptional.get());
        }
        else{
         throw new DataNotFoundException("question not found");
        }
    }

    public Map<String,Object> getPosts(Pageable pageable){

        Page<ProjectStudyPost> posts = projectStudyPostRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content",posts.getContent());
        response.put("totalElements", posts.getTotalElements()); // Total number of elements
        response.put("number", posts.getNumber());  // Current page
        response.put("totalPages", posts.getTotalPages()); // Total number of pages
        response.put("currentPage", posts.getNumber()); // Current page number
        response.put("pageSize", posts.getSize()); // Size of each page

        return response;
    }
}
