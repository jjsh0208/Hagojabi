package com.ddong_kka.hagojabi.ProjectStudyPost.Service;

import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.Exception.DataNotFoundException;
import com.ddong_kka.hagojabi.Exception.UnauthorizedAccessException;
import com.ddong_kka.hagojabi.Exception.UserNotFoundException;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.DTO.ProjectStudyPostDetailDTO;
import com.ddong_kka.hagojabi.ProjectStudyPost.Interface.ProjectStudyPostService;
import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class ProjectStudyPostServiceImpl implements ProjectStudyPostService {

    private final ProjectStudyPostRepository projectStudyPostRepository;
    private final UsersRepository usersRepository;

    public ProjectStudyPostServiceImpl(ProjectStudyPostRepository projectStudyPostRepository, UsersRepository usersRepository) {
        this.projectStudyPostRepository = projectStudyPostRepository;
        this.usersRepository = usersRepository;
    }

    public String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof PrincipalDetails) {
            return ((PrincipalDetails) principal).getUsername(); // JWT에 포함된 이메일 또는 사용자 이름
        } else if (principal != null) {
            return principal.toString();
        } else {
            throw new UserNotFoundException("Authentication object is invalid or null.");
        }
    }

    @Override
    public Long register(ProjectStudyPostDTO projectStudyPostDTO) {

        String userEmail = getAuthenticatedUserEmail();

        // Find the User by username in the database
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        LocalDate today = LocalDate.now();
        if (!projectStudyPostDTO.getRecruitmentDeadline().isAfter(today)) {
            throw new IllegalArgumentException("모집 마감일은 오늘 이후 날짜로 설정해야 합니다.");
        }

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

       return projectStudyPostRepository.save(projects).getId();
    }

    @Override
    public ProjectStudyPostDetailDTO getDetail(Long id) {
        ProjectStudyPost projectStudyPost = projectStudyPostRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("post not found with id : " + id));

        // 조회수 증가
        projectStudyPost.setViewCount(projectStudyPost.getViewCount() + 1);
        this.projectStudyPostRepository.save(projectStudyPost);

        // DTO 생성 시 현재 사용자 이메일 전달
        return new ProjectStudyPostDetailDTO(projectStudyPost, getAuthenticatedUserEmail());
    }


    @Override
    public Page<ProjectStudyPost> getPosts(Pageable pageable){
        return  projectStudyPostRepository.findAll(pageable);
    }

    @Override
    public Long update(ProjectStudyPostDTO projectStudyPostDTO, Long id) {

        ProjectStudyPost projectStudyPost = projectStudyPostRepository.findById(id)
                .orElseThrow(() ->  new DataNotFoundException("post not found with id : " + id));

        String currentUserEmail = getAuthenticatedUserEmail();
        if (!projectStudyPost.getUser().getEmail().equals(currentUserEmail)){
            throw new UnauthorizedAccessException("You are not Authentication to update this post : " + id);
        }

            projectStudyPost.setTitle(projectStudyPostDTO.getTitle());
            projectStudyPost.setDescription(projectStudyPostDTO.getDescription());
            projectStudyPost.setPosition(projectStudyPostDTO.getPosition());
            projectStudyPost.setPeopleCount(projectStudyPostDTO.getPeopleCount());
            projectStudyPost.setDuration(projectStudyPostDTO.getDuration());
            projectStudyPost.setProjectMode(projectStudyPostDTO.getProjectMode());
            projectStudyPost.setRecruitmentDeadline(projectStudyPostDTO.getRecruitmentDeadline());
            projectStudyPost.setTechStack(projectStudyPostDTO.getTechStack());
            projectStudyPost.setRecruitmentType(projectStudyPostDTO.getRecruitmentType());
            projectStudyPost.setContactEmail(projectStudyPostDTO.getContactEmail());

        return projectStudyPostRepository.save(projectStudyPost).getId();
    }

    @Override
    public void deletePost(Long id)  {
        ProjectStudyPost projectStudyPost = projectStudyPostRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("post not found with id : " + id));

        String currentUserEmail = getAuthenticatedUserEmail();

        if (!projectStudyPost.getUser().getEmail().equals(currentUserEmail)){
            throw new UnauthorizedAccessException("You are not authorized to delete this post.");
        }

        projectStudyPostRepository.deleteById(id);
    }
}
