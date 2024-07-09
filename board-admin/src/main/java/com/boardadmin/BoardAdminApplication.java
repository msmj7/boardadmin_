package com.boardadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.RoleRepository;
import com.boardadmin.user.repository.UserRepository;
import com.boardadmin.user.service.UserService;

@SpringBootApplication
public class BoardAdminApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(BoardAdminApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setRoleName("USER");
            roleRepository.save(userRole);
        }

        // 초기 관리자 계정 설정
        Optional<User> existingAdminUser = userRepository.findByUserIndex(1);
        if (existingAdminUser == null) {
            Role adminRole = roleRepository.findByRoleName("ADMIN");

            User adminUser = new User();
            adminUser.setUserId("pangpany");
            adminUser.setPassword(userService.encodePassword("pangpany2024"));
            adminUser.setEmail("admin@example.com");
            adminUser.setActive(true);
            adminUser.getRoles().add(adminRole); // 역할 추가
            userRepository.save(adminUser);
        }
    }

}
