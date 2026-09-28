package com.example.booking.config;

import com.example.booking.entity.Resource;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Default ADMIN user created.");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("User@123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            System.out.println("Default USER user created.");
        }

        if (resourceRepository.count() == 0) {
            Resource conferenceRoom = Resource.builder()
                    .name("Conference Room A")
                    .description("Large conference room with projector")
                    .type("Meeting Room")
                    .price(new BigDecimal("500.00"))
                    .available(true)
                    .build();

            Resource projector = Resource.builder()
                    .name("Projector")
                    .description("HD meeting room projector")
                    .type("Equipment")
                    .price(new BigDecimal("200.00"))
                    .available(true)
                    .build();

            Resource car = Resource.builder()
                    .name("Toyota Innova")
                    .description("Company vehicle for official use")
                    .type("Vehicle")
                    .price(new BigDecimal("800.00"))
                    .available(true)
                    .build();

            resourceRepository.save(conferenceRoom);
            resourceRepository.save(projector);
            resourceRepository.save(car);
            System.out.println("Sample resources created.");
        }
    }
}
