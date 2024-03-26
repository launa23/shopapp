package com.project.shopapp.configurations;

import com.project.shopapp.components.JwtTokenUtil;
import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Yêu cầu các request gửi đến phải có cái gì đó thì mới được đi qua
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    // Nhiệm vụ là chặn lại các request, kiểm tra xem có đủ yêu cầu không
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers(
                            String.format("%s/user/register", apiPrefix),
                            String.format("%s/user/login", apiPrefix)
                    ).permitAll()
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/order/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/order/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/order/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/order/**", apiPrefix)).hasRole(Role.ADMIN)
                            .anyRequest().authenticated();
                })
        ;
        return http.build();
    }
}
