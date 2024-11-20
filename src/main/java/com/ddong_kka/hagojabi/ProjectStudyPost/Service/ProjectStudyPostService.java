package com.ddong_kka.hagojabi.ProjectStudyPost.Service;

import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
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

        System.out.println("마감 일자 : " + projectStudyPostDTO.getRecruitmentDeadline());
        System.out.println("이메일 : " +  projectStudyPostDTO.getContactEmail());


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

    public ProjectStudyPost detail(Long id) {
        Optional<ProjectStudyPost> projectStudyPostOptional = projectStudyPostRepository.findById(id);
        return projectStudyPostOptional.orElse(null);
    }
}
