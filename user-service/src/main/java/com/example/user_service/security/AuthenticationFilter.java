package com.example.user_service.security;

import com.example.user_service.dto.UserDto;
import com.example.user_service.service.UserService;
import com.example.user_service.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

// /login 요청이 들어오면, 이메일/비밀번호를 읽는다. → 인증을 시도 → 인증에 성공하면 → 나중에 JWT를 발급하는 역할을 하는 클래스
// UsernamePasswordAuthenticationFilter : Spring Security가 기본 제공하는 로그인 처리 필터
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            // 클라이언트가 POST /login 요청할 때 JSON 바디에 담은 내용을 RequestLogin 객체로 파싱함
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            // 파싱된 RequestLogin 객체로 Spring Security의 인증 객체 UsernamePasswordAuthenticationToken을 만들어서
            // AuthenticationManager를 통해 인증을 시도
            // authenticate() 메서드 내부에서 UserDetailsService.loadUserByUsername() 호출해서 사용자 정보를 가져온다.
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 인증 성공했을 때 실행됨
    // JWT 토큰을 생성해서 헤더에 실어주는 작업
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // 로그인에 성공한 사용자의 이메일을 가져오는 코드
        // authResult 안에 인증된 사용자 정보가 들어 있고, 그걸 User 객체로 꺼낸다.
        String userName = ((User)authResult.getPrincipal()).getUsername();

        // DB에 있는 사용자 상세 정보를 이메일로 다시 조회
        // UserDetails에는 딱 로그인용 정보만 들어 있어서,
        // 추가 정보가 필요하면 UserDto 같은 걸로 내가 원하는 구조로 다시 불러오는 게 일반적
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        byte[] secretKeyBytes = Base64.getEncoder().encode(env.getProperty("token.secret").getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        String token = Jwts.builder()
                .setSubject(userDetails.getEmail())
                .setExpiration(Date.from(now.plusMillis(Long.parseLong(env.getProperty("token.expiration_time")))))
                .setIssuedAt(Date.from(now))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getEmail());
    }
}
