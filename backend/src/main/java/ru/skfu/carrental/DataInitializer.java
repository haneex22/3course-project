package ru.skfu.carrental;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.carrental.foundation.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String encoded = passwordEncoder.encode("password123");
        userRepository.findByEmail("admin@carrent.ru").ifPresent(u -> {
            u.setPasswordHash(encoded);
            userRepository.save(u);
        });
        userRepository.findByEmail("manager@carrent.ru").ifPresent(u -> {
            u.setPasswordHash(encoded);
            userRepository.save(u);
        });
        userRepository.findByEmail("client@carrent.ru").ifPresent(u -> {
            u.setPasswordHash(encoded);
            userRepository.save(u);
        });
    }
}
