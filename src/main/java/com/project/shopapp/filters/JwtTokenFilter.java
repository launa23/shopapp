package com.project.shopapp.filters;

import com.project.shopapp.components.JwtTokenUtil;
import com.project.shopapp.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Kiểm tra các request có token ko or có hợp lệ ko
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        //filterChain.doFilter(request, response);        // cho đi qua hết
        try {
            if (isBypassToken(request)){
                filterChain.doFilter(request, response);
                return;
            }
            String authenHeader = request.getHeader("Authorization");
            // Kiểm tra xem request có token không
            if (authenHeader == null || !authenHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            final String token = authenHeader.substring(7);         // cắt bỏ chữ "Bearer " trong chuỗi bearer token để lấy token
            final String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);      // Lấy ra phone number từ token
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null){
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if (jwtTokenUtil.validtionToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);

        }
        catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

    private boolean isBypassToken (@NonNull HttpServletRequest request){
        // Danh sách các request được phép đi qua
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/user/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/user/login", apiPrefix), "POST")
        );
        for(Pair<String, String> bypassToken: bypassTokens){
            if (request.getServletPath().contains(bypassToken.getLeft()) &&
                request.getMethod().equals(bypassToken.getRight())){
                return true;
            }
        }
        return false;
    }
}
