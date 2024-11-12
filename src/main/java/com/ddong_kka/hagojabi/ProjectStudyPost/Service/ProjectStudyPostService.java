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

@Service
public class ProjectStudyPostService {

    private final ProjectStudyPostRepository projectStudyPostRepository;
    private final UsersRepository usersRepository;

    public ProjectStudyPostService(ProjectStudyPostRepository projectStudyPostRepository, UsersRepository usersRepository) {
        this.projectStudyPostRepository = projectStudyPostRepository;
        this.usersRepository = usersRepository;
    }

    public void register(ProjectStudyPostDTO projectStudyPostDTO) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        // Find the User by username in the database
        Users user = usersRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        String positionsString = String.join(",", projectStudyPostDTO.getPosition());
        String techStacksString = String.join(",", projectStudyPostDTO.getTechStack());

        System.out.println(projectStudyPostDTO.getRecruitmentType());


        ProjectStudyPost projects = ProjectStudyPost.builder()
                .title(projectStudyPostDTO.getTitle())
                .description(projectStudyPostDTO.getDescription())
                .position(positionsString)
                .peopleCount(projectStudyPostDTO.getPeopleCount())
                .duration(projectStudyPostDTO.getDuration())
                .projectMode(projectStudyPostDTO.getProjectMode())
                .recruitmentDeadline(projectStudyPostDTO.getRecruitmentDeadline())
                .techStack(techStacksString)
                .recruitmentType(projectStudyPostDTO.getRecruitmentType())
                .contactEmail(projectStudyPostDTO.getContactEmail())
                .user(user)
                .build();

        projectStudyPostRepository.save(projects);

    }
}
