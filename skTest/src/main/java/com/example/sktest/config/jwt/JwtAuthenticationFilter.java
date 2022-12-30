package com.example.sktest.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.sktest.config.auth.PrincipalDetails;
import com.example.sktest.model.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        try{
            System.out.println("i am in JwtAuthenticationFilter");
            ObjectMapper om = new ObjectMapper();

            Users user = om.readValue(request.getInputStream(),Users.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨 "+principalDetails.getUser().getUsername()); //로그인 정상 되었다.

            return authentication;

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException{
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                .withClaim("id",principalDetails.getUser().getId())
                .withClaim("username",principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos"));
        response.addHeader("Authorization","Bearer "+jwtToken);
    }
}
