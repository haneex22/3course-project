package ru.skfu.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skfu.carrental.entity.User;
import ru.skfu.carrental.entity.enums.UserRole;
import ru.skfu.carrental.foundation.UserRepository;
import ru.skfu.carrental.security.JwtTokenProvider;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    private String validToken;
    private UUID carId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@carrent.ru");
        user.setPasswordHash("password");
        user.setRole(UserRole.CLIENT);
        userRepository.save(user);

        validToken = jwtTokenProvider.generateToken(user);
    }

    @Test
    void getCars_authorized_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCarById_nonExistent_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/cars/00000000-0000-0000-0000-000000099999")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNotFound());
    }
}
