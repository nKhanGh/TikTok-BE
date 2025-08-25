package com.tiktok.demo.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tiktok.demo.dto.response.PermissionResponse;
import com.tiktok.demo.entity.Permission;
import com.tiktok.demo.entity.Role;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.repository.PermissionRepository;
import com.tiktok.demo.repository.RoleRepository;
import com.tiktok.demo.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    String admin = "SUPER_ADMIN";

    @Bean
    ApplicationRunner applicationRunner(
        UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository
    ) {
        return args -> {
            if(!userRepository.existsByUsername(admin)){
                var permissions = new HashSet<Permission>();
                Role role = Role.builder()
                    .name(admin)
                    .description("Highest-level administrator with full system access")
                    .permissions(permissions)
                    .build();
                roleRepository.save(role);
                var roles = new HashSet<Role>();
                roles.add(role);
                User user = User.builder()
                    .name(admin)
                    .username(admin)
                    .password(passwordEncoder.encode(admin))
                    .isVerified(true)
                    .roles(roles)
                    .build();
                userRepository.save(user);
                log.info("User SUPER_ADMIN has been created with password \"SUPER_ADMIN\", please change it if needed!");
            }
        };
    }

}
