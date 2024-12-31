package com.ddong_kka.hagojabi.Users.Service;

import com.ddong_kka.hagojabi.Config.JWT.JwtUtil;
import com.ddong_kka.hagojabi.Config.auth.PrincipalDetails;
import com.ddong_kka.hagojabi.Exception.EmailDuplicateException;
import com.ddong_kka.hagojabi.Exception.InvalidPasswordException;
import com.ddong_kka.hagojabi.Exception.UserNotFoundException;
import com.ddong_kka.hagojabi.Users.DTO.UserDetailDTO;
import com.ddong_kka.hagojabi.Users.DTO.UsersDTO;
import com.ddong_kka.hagojabi.Users.Interface.UsersService;
import com.ddong_kka.hagojabi.Users.Model.Users;
import com.ddong_kka.hagojabi.Users.Repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder encoder;

    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
    }

    public String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof PrincipalDetails) {
            return ((PrincipalDetails) principal).getUsername(); // JWT에 포함된 이메일 또는 사용자 이름
        } else if (principal != null) {
            return principal.toString();
        } else {
            throw new UserNotFoundException("인증객체가 유효하지않거나 존재하지않습니다.");
        }
    }


    public boolean existsByEmail(String email){
        return usersRepository.existsByEmail(email);
    }

    @Override
    public void register(UsersDTO usersDTO){

        if (existsByEmail(usersDTO.getEmail())){
            throw new EmailDuplicateException("이미 사용중인 이메일입니다. : "+ usersDTO.getEmail());
        }

        validatePassword(usersDTO.getPassword());

        String encPassword = encoder.encode(usersDTO.getPassword());

        Users users = Users.builder()
                .email(usersDTO.getEmail())
                .username(usersDTO.getUsername())
                .password(encPassword)
                .role("ROLE_UNVERIFIED")
                .build();

        usersRepository.save(users);

    }

    @Override
    public UserDetailDTO getUserInfo() {

        String userEmail = getAuthenticatedUserEmail();

        Users users = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("해당 이메일로 사용자를 찾을 수 없습니다. : " + userEmail));

        return new UserDetailDTO(users);
    }

    @Override
    public UserDetailDTO userNameUpdate(UserDetailDTO userDetailDTO) {

        if (userDetailDTO.getEmail() == null || userDetailDTO.getUsername() == null){
            throw new IllegalArgumentException("Email과 사용자 이름은 필수 입력입니다.");
        }

        Users users = usersRepository.findByEmail(userDetailDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("해당 이메일로 사용자를 찾을 수 없습니다. : " + userDetailDTO.getEmail()));

        users.setUsername(userDetailDTO.getUsername());

        // 변경 후 저장
        usersRepository.save(users);

        return new UserDetailDTO(users);
    }

    @Override
    public void passwordChange(UserDetailDTO userDetailDTO) {

        // 입력 데이터 검증
        if (userDetailDTO.getPassword() == null || userDetailDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수 입력입니다.");
        }

        validatePassword(userDetailDTO.getPassword());

        String userEmail = getAuthenticatedUserEmail();

        Users users = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("해당 이메일로 사용자를 찾을 수 없습니다. : " + userEmail));

        // 새로운 비밀번호 암호화 및 저장
        String encPassword = encoder.encode(userDetailDTO.getPassword());
        users.setPassword(encPassword);

        usersRepository.save(users);
    }

    private void validatePassword(String password) {
        // 비밀번호가 null 또는 빈 문자열인지 확인
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException("비밀번호는 필수 입력 항목입니다.");
        }

        // 비밀번호 최소 길이 확인 (예: 8자 이상)
        if (password.length() < 8) {
            throw new InvalidPasswordException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        // 비밀번호에 대문자 포함 여부 확인
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("비밀번호에는 최소 1개의 대문자가 포함되어야 합니다.");
        }

        // 비밀번호에 숫자 포함 여부 확인
        if (!password.matches(".*[0-9].*")) {
            throw new InvalidPasswordException("비밀번호에는 최소 1개의 숫자가 포함되어야 합니다.");
        }

        // 비밀번호에 특수 문자 포함 여부 확인
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new InvalidPasswordException("비밀번호에는 최소 1개의 특수 문자가 포함되어야 합니다.");
        }
    }
}
