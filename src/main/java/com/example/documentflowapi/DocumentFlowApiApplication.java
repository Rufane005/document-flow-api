package com.example.documentflowapi;

import com.example.documentflowapi.model.User;
import com.example.documentflowapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DocumentFlowApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentFlowApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner setupDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User user = new User();
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode("password"));
                userRepository.save(user);
                System.out.println("Defolt istifadəçi 'admin' yaradıldı.");
            }
        };
    }
}