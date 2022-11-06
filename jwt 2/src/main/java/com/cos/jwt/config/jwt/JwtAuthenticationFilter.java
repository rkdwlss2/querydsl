package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음
// /login 요청해서 username,password 전송하면 (post)
// usernamePasswordAuthenticationFilter 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도중");

        // 1. username, password 받아서
        try {
//            BufferedReader br = request.getReader();
//
//            String input = null;
//            while((input = br.readLine())!=null){
//                System.out.println(input);
//            }

            ObjectMapper om = new ObjectMapper(); // json 데이터 파싱 쉽게 해줌

            User user = om.readValue(request.getInputStream(),User.class);

            System.out.println(user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());

            //PrincipalDetailsService 의 loadUserBtUsername()함수가 실행됨
            Authentication authentication = authenticationManager.authenticate(authenticationToken); // 인증을 해줌

            // authentication 객체가 session영역에 저장됨 => 로그인이 되었다는 뜻
            // DB에 있는 username 과 password가 일치한다.
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            System.out.println("로그인 완료됨 "+principalDetails.getUser().getUsername()); //로그인 정상 되었다.
            // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하게 하려고
            // jwt 토큰을 사용하면서 세션을 만들 이유가 없음, 근데 단지 권한 처리 때문에 session 너헝 줍니다.
            return authentication;
//           System.out.println(request.getInputStream().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return super.attemptAuthentication(request,response);]
        return null;
    }

    // 뒤에 실행되는 함수가 잇음

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication : 실행됨 = 인증 완료됬다는뜻");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                        .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                                .withClaim("id",principalDetails.getUser().getId())
                                        .withClaim("username",principalDetails.getUser().getUsername())
                                                .sign(Algorithm.HMAC512("cos"));

    response.addHeader("Authorization","Bearer "+jwtToken);
//        super.successfulAuthentication(request, response, chain, authResult);
    }
}
