package com.project.shopapp.configurations;

import com.project.shopapp.components.JwtTokenUtil;
import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

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
                            String.format("%s/user/login", apiPrefix),
                            String.format("%s/products", apiPrefix),
                            String.format("%s/categories", apiPrefix),
                            String.format("%s/order", apiPrefix)

                    ).permitAll()
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/order/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/order/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/categories/delete/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/categories/update/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/order/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/order/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products/image/*", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products/*", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products/category/*", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/categories/*", apiPrefix)).permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);
        // Lỗi do của mình là http chứ không phải https nên nó sẽ chặn lại, nếu deploy lên hosting thì sẽ có https nên sau này nên bỏ đoạn dưới này đi
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        ;
        return http.build();
    }
}
