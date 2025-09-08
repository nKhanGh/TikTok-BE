package com.tiktok.demo.security.oauth2;

import com.tiktok.demo.entity.User;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.repository.UserRepository;
import com.tiktok.demo.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    AuthenticationService authenticationService;
    UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            // Fallback method: find user by email từ OAuth2 attributes
            if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
                String email = oAuth2User.getAttribute("email");
                log.info("Looking for user by email: {}", email);

                User user = userRepository.findByEmailWithRoles(email)
                        .orElseThrow(() -> {
                            log.error("User not found with email: {}", email);
                            return new AppException(ErrorCode.USER_NOT_EXISTED);
                        });

                log.info("Found user: {}, roles count: {}", user.getId(), user.getRoles().size());

                // Force initialize roles collection trong transaction
//                user.getRoles().size(); // Trigger lazy loading

                String token = authenticationService.generateToken(user);

                String frontendUrl = "http://localhost:5173/auth/callback?token=" + token;
                response.sendRedirect(frontendUrl);

            } else {
                log.error("Unknown principal type: {}", authentication.getPrincipal().getClass());
                response.sendRedirect("http://localhost:5173/auth/error?message=unknown_principal_type");
            }

        } catch (Exception e) {
            log.error("Error during OAuth2 success handling", e);
            response.sendRedirect("http://localhost:5173/auth/error?message=" + e.getMessage());
        }
    }
}