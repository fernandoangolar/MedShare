package ao.com.angotech.angotech.controller;

import ao.com.angotech.application.controllers.AuthController;
import ao.com.angotech.application.dtos.auth.RegisterRequest;
import ao.com.angotech.application.mappers.UserMapper;
import ao.com.angotech.application.usecases.auth.RegisterUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module());


    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void deveRejeitarNomeVazioOUCurto() throws Exception {
        RegisterRequest request = new RegisterRequest("", "teste@email.com", "Senha1", "USER");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.name").exists());

    }

    @Test
    void deveRejeitarEmailInvalido() throws Exception {
        RegisterRequest request = new RegisterRequest("Teste", "teste@email", "Senha1", "USER");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.email").exists());

    }

    @Test
    void deveRejeitarSenhaFraca() throws Exception {
        RegisterRequest request = new RegisterRequest("Teste", "teste@email.com", "123456", "USER");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.password").exists());

    }

    @Test
    void deveRejeitarSenhaCurta() throws Exception {
        RegisterRequest request = new RegisterRequest("Teste", "teste@email.com", "12345", "USER");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.password").exists());

    }

    @Test
    void deveRejeitarRoleNulo() throws Exception {
        RegisterRequest request = new RegisterRequest("Joao", "joao@email.com", "Senha1", "USER");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.role").exists());
    }

}
