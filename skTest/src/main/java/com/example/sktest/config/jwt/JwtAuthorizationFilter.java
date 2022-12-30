package com.example.sktest.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.sktest.config.auth.PrincipalDetails;
import com.example.sktest.model.Users;
import com.example.sktest.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,UserRepository userRepository){
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws IOException, ServletException {
        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }
        String jwtToken =request.getHeader("Authorization").replace("Bearer ","");

        String username = JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtToken).getClaim("username").asString();

        if (username!=null){
            Users userEntity =userRepository.findByUsername(username);
            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails,null,principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }

}
