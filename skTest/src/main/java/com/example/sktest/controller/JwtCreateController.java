package com.example.sktest.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.sktest.config.oauth.provider.FacebookUser;
import com.example.sktest.config.oauth.provider.GoogleUser;
import com.example.sktest.config.oauth.provider.OAuthUserInfo;
import com.example.sktest.model.Users;
import com.example.sktest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwtCreateController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @PostMapping("/oauth/jwt/google")
    public String jwtCreate(@RequestBody Map<String,Object> data){
        System.out.println("jwtCreate 실행됨");
        System.out.println(data.get("profileObj"));
        OAuthUserInfo googleUser = new GoogleUser((Map<String,Object>)data.get("profileObj"));
        Users userEntity = userRepository.findByUsername(googleUser.getProvider()+"_"+googleUser.getProviderId());

        if (userEntity == null){
            Users userRequest = Users.builder()
                    .username(googleUser.getProvider()+"_"+googleUser.getProviderId())
                    .password(bCryptPasswordEncoder.encode("겟인데어"))
                    .email(googleUser.getEmail())
                    .provider(googleUser.getProvider())
                    .providerId(googleUser.getProviderId())
                    .roles("ROLE_ADMIN")
                    .build();
            userEntity = userRepository.save(userRequest);
        }
        String jwtToken = JWT.create()
                .withSubject(userEntity.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                .withClaim("id",userEntity.getId())
                .withClaim("username",userEntity.getUsername())
                .sign(Algorithm.HMAC512("cos"));
        return jwtToken;
    }

    @PostMapping("/oauth/jwt/facebook")
    public String faceBookJwtCreate(@RequestBody Map<String,Object> data){
        System.out.println("facebookjwtCreate 실행됨");
        System.out.println(data.get("profileObj"));
        OAuthUserInfo facebookUser = new FacebookUser(data);
        Users userEntity = userRepository.findByUsername(facebookUser.getProvider()+"_"+facebookUser.getProviderId());

        if (userEntity == null){
            Users userRequest = Users.builder()
                    .username(facebookUser.getProvider()+"_"+facebookUser.getProviderId())
                    .password(bCryptPasswordEncoder.encode("겟인데어"))
                    .email(facebookUser.getEmail())
                    .provider(facebookUser.getProvider())
                    .providerId(facebookUser.getProviderId())
                    .roles("ROLE_ADMIN")
                    .build();
            userEntity = userRepository.save(userRequest);
        }
        String jwtToken = JWT.create()
                .withSubject(userEntity.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                .withClaim("id",userEntity.getId())
                .withClaim("username",userEntity.getUsername())
                .sign(Algorithm.HMAC512("cos"));
        return jwtToken;
    }
}
