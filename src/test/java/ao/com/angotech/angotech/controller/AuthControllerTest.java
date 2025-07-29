package ao.com.angotech.angotech.controller;

import ao.com.angotech.application.controllers.AuthController;
import ao.com.angotech.application.dtos.auth.LoginRequest;
import ao.com.angotech.application.dtos.auth.LoginResponse;
import ao.com.angotech.application.usecases.auth.AuthenticationUseCase;
import ao.com.angotech.application.usecases.auth.RegisterUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.naming.AuthenticationException;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module());


    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private AuthenticationUseCase authenticationUseCase;

    private LoginRequest request;
    private LoginResponse response;

    @BeforeEach
    void setUp() {
        request = new LoginRequest("test@email.com", "1234567A");
        response = new LoginResponse("deee11b9-6db4-4997-9cfc-030f592d3198", "Test User",
                "test@email.com", "123457A");
    }

    @Test
    @DisplayName("Deve retornar 200 e LoginResponse quando autenticação é bem-sucedida")
    void shouldReturn200AndLoginResponse_WhenAuthenticationIsSuccessful() throws Exception {
        // Given
        when(authenticationUseCase.authentication(any(LoginRequest.class)))
                .thenReturn(response);

        // When e Then
        mockMvc.perform(post("/api/v1/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("deee11b9-6db4-4997-9cfc-030f592d3198"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.token").value("123457A"));

        verify(authenticationUseCase).authentication(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando credenciais são inválidas")
    void shouldReturn400_WhenCredentialsAreInvalid() throws Exception {
        // Given
        when(authenticationUseCase.authentication(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("E-mail ou senha incorreta"));

        // When e Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("E-mail ou senha incorreta"));

        verify(authenticationUseCase).authentication(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando AuthenticationException é lançada")
    void shouldReturn400_WhenAuthenticationExceptionIsThrown() throws Exception {
        // Given
        when(authenticationUseCase.authentication(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciais Inválidas"));

        verify(authenticationUseCase).authentication(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 422 quando email é inválido")
    void shouldReturn422_WhenEmailIsInvalid() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("invalid-email", "Password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.email").exists());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 422 quando email está em branco")
    void shouldReturn422_WhenEmailIsBlank() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("", "Password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.email").exists());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 422 quando senha está em branco")
    void shouldReturn422_WhenPasswordIsBlank() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("test@email.com", "");

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 422 quando senha é muito curta")
    void shouldReturn422_WhenPasswordIsTooShort() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("test@email.com", "123A");

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 422 quando senha é muito longa")
    void shouldReturn422_WhenPasswordIsTooLong() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("test@email.com", "123A1234567");

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 422 quando senha não tem letra maiúscula")
    void shouldReturn422_WhenPasswordHasNoUpperCase() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("test@email.com", "passwor1");

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 422 quando senha não tem número")
    void shouldReturn422_WhenPasswordHasNoNumber() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("test@email.com", "password");

        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 400 quando Content-Type não é JSON")
    void shouldReturn400_WhenContentTypeIsNotJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("invalid content"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 400 quando corpo da requisição está vazio")
    void shouldReturn400_WhenRequestBodyIsEmpty() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationUseCase);
    }

    @Test
    @DisplayName("Deve retornar 400 quando JSON é inválido")
    void shouldReturn400_WhenJsonIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationUseCase);
    }


    @Test
    @DisplayName("Deve chamar AuthService com parâmetros corretos")
    void shouldCallAuthService_WithCorrectParameters() throws Exception {
        // Given
        when(authenticationUseCase.authentication(any(LoginRequest.class)))
                .thenReturn(response);

        // When
        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then
        ArgumentCaptor<LoginRequest> requestCaptor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(authenticationUseCase).authentication(requestCaptor.capture());

        LoginRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.email()).isEqualTo("test@email.com");
        assertThat(capturedRequest.password()).isEqualTo("1234567A");
    }

    @Test
    @DisplayName("Deve aceitar diferentes métodos HTTP apenas POST")
    void shouldAcceptOnlyPostMethod() throws Exception {
        // Given
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then - GET deve retornar 405
        mockMvc.perform(get("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isMethodNotAllowed());

        // PUT deve retornar 405
        mockMvc.perform(put("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isMethodNotAllowed());

        // DELETE deve retornar 405
        mockMvc.perform(delete("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(authenticationUseCase);
    }
}
