package com.ddong_kka.hagojabi.Projects.Service;

import com.ddong_kka.hagojabi.Projects.DTO.ProjectsDTO;
import com.ddong_kka.hagojabi.Projects.Model.Projects;
import com.ddong_kka.hagojabi.Projects.Repository.ProjectsRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectsService {

    private final ProjectsRepository projectsRepository;

    public ProjectsService(ProjectsRepository projectsRepository) {
        this.projectsRepository = projectsRepository;
    }

    public void register(ProjectsDTO projectsDTO) {

        System.out.println(projectsDTO.getDescription());

        Projects projects = Projects.builder()
                .title(projectsDTO.getTitle())
                .description(projectsDTO.getDescription())
                .build();


        projectsRepository.save(projects);

    }
}
