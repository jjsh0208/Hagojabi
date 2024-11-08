package com.ddong_kka.hagojabi.Projects.Controller;

import com.ddong_kka.hagojabi.Projects.DTO.ProjectsDTO;
import com.ddong_kka.hagojabi.Projects.Service.ProjectsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectsRestController {

    private final ProjectsService projectsService;

    public ProjectsRestController(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    @PostMapping("/projects/ProjectCreate")
    public ResponseEntity<?> registerProject(@RequestBody ProjectsDTO projectsDTO){


        System.out.println("동작");
        System.out.println(projectsDTO.getDescription());
        System.out.println(projectsDTO.getTitle());
        projectsService.register(projectsDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 작성 완료.");
    }
}
