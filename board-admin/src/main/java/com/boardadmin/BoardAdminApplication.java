package com.boardadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.RoleRepository;
import com.boardadmin.user.repository.UserRepository;

@SpringBootApplication
public class BoardAdminApplication implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
    
    @Value("${admin.userId}")
    private String adminUserId;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

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
        User existingAdminUser = userRepository.findByUserId(adminUserId);
        if (existingAdminUser == null) {
            Role adminRole = roleRepository.findByRoleName("ADMIN");
            User adminUser = new User();
            adminUser.setUserId(adminUserId);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setEmail(adminEmail);
            adminUser.setActive(true);
            adminUser.getRoles().add(adminRole); // 역할 추가해줌
            userRepository.save(adminUser);
        }
    }
}
