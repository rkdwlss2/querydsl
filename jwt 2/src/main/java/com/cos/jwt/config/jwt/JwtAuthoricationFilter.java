package com.cos.jwt.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthoricationFilter extends BasicAuthenticationFilter {

    public JwtAuthoricationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨.");
    }
}
