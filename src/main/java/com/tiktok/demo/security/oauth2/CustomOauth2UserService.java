package com.tiktok.demo.security.oauth2;

import com.tiktok.demo.dto.request.UserCreationRequest;
import com.tiktok.demo.dto.response.UserPrivateResponse;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.repository.UserRepository;
import com.tiktok.demo.service.AuthenticationService;
import com.tiktok.demo.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    UserRepository userRepository;
    UserService userService;
    AuthenticationService authenticationService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            if (email == null) {
                log.error("Email is null from OAuth2 provider");
                throw new AppException(ErrorCode.EMAIL_INVALID);
            }

            // Check if user already exists
            var existingUser = userRepository.findByEmail(email);
            String userId;

            if(existingUser.isEmpty()) {
                log.info("User not found, creating new user for email: {}", email);

                String username = authenticationService.generateUsername();
                log.info("Generated username: {}", username);

                UserPrivateResponse response = userService.createUser(UserCreationRequest.builder()
                        .email(email)
                        .username(username)
                        .name(name)
                        .bio("No bio yet.")
                        .build(), true
                );
                userId = response.getId();
                log.info("Created new user with ID: {}", userId);

                // Verify the user was created
                User createdUser = userRepository.findById(userId).orElse(null);
                if (createdUser != null) {
                    log.info("User creation verified - ID: {}, Email: {}", createdUser.getId(), createdUser.getEmail());
                } else {
                    log.error("User creation failed - cannot find user with ID: {}", userId);
                }

            } else {
                User user = existingUser.get();
                userId = user.getId();
                log.info("Found existing user with ID: {}, Email: {}", userId, user.getEmail());
            }

            log.info("Creating CustomOAuth2User with userId: {}", userId);
            CustomOAuth2User customUser = new CustomOAuth2User(oAuth2User, userId);
            log.info("CustomOAuth2User created, getName() returns: {}", customUser.getName());
            log.info("=== OAuth2 user loading completed ===");

            return customUser;

        } catch (Exception e) {
            log.error("Error loading OAuth2 user", e);
            throw new RuntimeException("Failed to load OAuth2 user", e);
        }
    }
}
