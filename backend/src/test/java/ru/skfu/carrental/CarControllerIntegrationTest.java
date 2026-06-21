package ru.skfu.carrental;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.skfu.carrental.foundation.CarRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/seed-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String getJwtToken() throws Exception {
        String body = """
                {
                    "email": "client@carrent.ru",
                    "password": "password123"
                }
                """;
        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        // Parse token from JSON response: {"token":"...","email":"...","role":"..."}
        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void getCars_unauthorized_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCars_authorized_returns200() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/api/v1/cars")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCars_withClassFilter_returnsFiltered() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/api/v1/cars?carClass=ECONOMY")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    @Test
    void getCarById_existingCar_returns200() throws Exception {
        String token = getJwtToken();

        String carsJson = mockMvc.perform(get("/api/v1/cars")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String firstId = objectMapper.readTree(carsJson).get(0).get("id").asText();

        mockMvc.perform(get("/api/v1/cars/" + firstId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").isNotEmpty());
    }

    @Test
    void getCarById_nonExistent_returns404() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/api/v1/cars/00000000-0000-0000-0000-000000099999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
