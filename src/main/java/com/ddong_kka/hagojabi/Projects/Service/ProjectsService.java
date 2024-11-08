package com.ddong_kka.hagojabi.Projects.Service;

import com.ddong_kka.hagojabi.Projects.DTO.ProjectsDTO;
import com.ddong_kka.hagojabi.Projects.Model.Projects;
import com.ddong_kka.hagojabi.Projects.Repository.ProjectsRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ProjectsService {

    private final ProjectsRepository projectsRepository;
    private final UsersRepository usersRepository;

    public ProjectsService(ProjectsRepository projectsRepository, UsersRepository usersRepository) {
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
    }

    public void register(ProjectsDTO projectsDTO) {

        System.out.println("입력받은 글  : " + projectsDTO.getDescription());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
            System.out.println("트루");
        } else {
            userEmail = principal.toString();
            System.out.println("펄스");
        }

        System.out.println("회원객체 : " + userEmail);


        // Find the User by username in the database
        Users user = usersRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        Projects projects = Projects.builder()
                .title(projectsDTO.getTitle())
                .description(projectsDTO.getDescription())
                .user(user)
                .build();


        projectsRepository.save(projects);

    }
}
