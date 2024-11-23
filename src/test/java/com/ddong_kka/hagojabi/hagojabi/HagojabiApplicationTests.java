package com.ddong_kka.hagojabi.hagojabi;

import com.ddong_kka.hagojabi.ProjectStudyPost.Model.ProjectStudyPost;
import com.ddong_kka.hagojabi.ProjectStudyPost.Repository.ProjectStudyPostRepository;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.hibernate.annotations.SecondaryRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PostCreationTest {

	@Autowired
	private ProjectStudyPostRepository projectStudyPostRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Test
	@Rollback(value = false)
	void createPostTest() {
		// Step 1: Retrieve a user from the database
		Users user = usersRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));

// Step 2: Create 100 new posts using the ProjectStudyPost builder
		for (int i = 0; i < 100; i++) {
			ProjectStudyPost post = ProjectStudyPost.builder()
					.title("Sample Post Title" + i) // Dynamic title with index
					.description("This is a test description for the new post.")
					.create_at(LocalDateTime.now())
					.update_at(LocalDateTime.now())
					.position(Arrays.asList("Frontend", "Backend"))
					.peopleCount("5")
					.duration("12")
					.projectMode("Online")
					.recruitmentDeadline(LocalDate.now().plusDays(30))
					.techStack(Arrays.asList("Java", "Spring", "React"))
					.recruitmentType("Full-time")
					.contactEmail("testuser@example.com")
					.user(user)
					.build();

			// Step 3: Save the post to the repository
			ProjectStudyPost savedPost = projectStudyPostRepository.save(post);

			// Step 4: Assert that the post was successfully saved
			assertNotNull(savedPost.getId(), "The post ID should not be null after saving.");
			// Assert that the title matches the dynamically assigned value
			assertEquals("Sample Post Title" + i, savedPost.getTitle(), "The post title should match.");
			assertEquals("testuser@example.com", savedPost.getContactEmail(), "The post contact email should match.");
		}





	}
}
