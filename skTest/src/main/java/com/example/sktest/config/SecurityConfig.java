package com.example.sktest.config;


import com.example.sktest.config.jwt.JwtAuthenticationFilter;
import com.example.sktest.config.jwt.JwtAuthorizationFilter;
import com.example.sktest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new MyCustomDsl())
                .and()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/user/**")
                        .access(new WebExpressionAuthorizationManager("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')"))
                        .requestMatchers("/api/v1/manager/**")
                        .access(new WebExpressionAuthorizationManager("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')"))
                        .requestMatchers("/api/v1/admin/**")
                        .access(new WebExpressionAuthorizationManager("hasRole('ROLE_ADMIN')"))
                        .anyRequest().permitAll()
                )
        ;

        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl,HttpSecurity>{
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
        }
    }


}
