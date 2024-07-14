package com.boardadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.boardadmin.common.util.*;
import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.repository.RoleRepository;
import com.boardadmin.user.repository.UserRepository;

@SpringBootApplication
public class BoardAdminApplication implements CommandLineRunner {
	//private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(BoardAdminApplication.class, args);
    }

    //예외처리 따로 더 추가하기!!!
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
        
        //초기 관리자 계정 설정
        User existingAdminUser = userRepository.findByUserId("pangpany");
        if (existingAdminUser == null) {
            Role adminRole = roleRepository.findByRoleName("ADMIN");
            User adminUser = new User();
            adminUser.setUserId("pangpany");
            adminUser.setPassword(passwordEncoder.encode("pangpany2024"));
            adminUser.setEmail("admin@pangpany.com");
            adminUser.setActive(true);
            adminUser.getRoles().add(adminRole); //역할 추가해줌
            userRepository.save(adminUser);

        }
    }

}
