package com.ddong_kka.hagojabi.Config.auth;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인이 진행디 완료가 되면 시큐리티 session을 만들어줘야한다. (Security ContextHolder)라는 키값에 다가 세션 정보를 저장한다.
// 이 세션에 들어갈 수 있는 정보는 오브젝트가 정해져있다 -> Authentication 타입 객체
// Authentization 안에 User 정보가 있어야된다.
//user 오브젝트 타입 -> UserDetails 타입 객체

// security session -> Authentication - > UserDetails 타입이다.
// 세션 정보를 꺼내면 Authentication를 가져와 그 안의 UserDetails(PrincipalDetails) 로 유저 정보에 접근한다.

import com.ddong_kka.hagojabi.User.Model.Users;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails , OAuth2User {

    private Users users; //콤포지션
    private Map<String,Object> attributes;

    
    //일반 로그인
    public PrincipalDetails(Users users) {
        super();
        this.users = users;
    }

    //oauth2 로그인
    public PrincipalDetails(Users users , Map<String,Object> attributes) {
        super();
        this.users = users;
        this.attributes = attributes;
    }



    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    //해당 User의 권한을 리턴 하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return users.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail();
    }


    //oauth2User 메서드
    @Override
    public String getName() {
        return null;
    }


    //json으로 반환되는 회원 정보를 map 으로 가져온다.
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
