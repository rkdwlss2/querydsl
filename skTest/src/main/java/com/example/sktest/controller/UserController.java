package com.example.sktest.controller;

import com.example.sktest.config.auth.PrincipalDetails;
import com.example.sktest.model.Users;
import com.example.sktest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

//    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/home")
    public String home(){
        return "<h1>home</h1>";
    }

    @PostMapping("/token")
    public String token(){
        return "<h1>token</h1>";
    }

    // Tip : JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가능.
    // 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문이다.

    // 유저 혹은 매니저 혹은 어드민이 접근 가능
    @GetMapping("user")
    public String user(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return "<h1>user</h1>";
    }

    // 매니저 혹은 어드민이 접근 가능
    @GetMapping("manager/reports")
    public String reports() {
        return "<h1>reports</h1>";
    }

    // 어드민이 접근 가능
    @GetMapping("admin/users")
    public List<Users> users(){
        return userRepository.findAll();
    }

    @PostMapping("join")
    public String join(@RequestBody Users user) {
        System.out.println("i am in join ========");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_ADMIN");
        userRepository.save(user);
        return "회원가입완료";
    }
}
