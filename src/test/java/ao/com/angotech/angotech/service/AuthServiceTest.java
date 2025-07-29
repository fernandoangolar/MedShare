package ao.com.angotech.angotech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ao.com.angotech.application.dtos.auth.LoginRequest;
import ao.com.angotech.application.dtos.auth.LoginResponse;
import ao.com.angotech.application.usecases.auth.AuthenticationUseCase;
import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.repository.UserRepository;
import ao.com.angotech.infrastructure.jwt.JwtToken;
import ao.com.angotech.infrastructure.jwt.JwtUserDetailsService;
import ao.com.angotech.infrastructure.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUserDetailsService detailsService;

    @InjectMocks
    private AuthenticationUseCase authenticationUseCase;

    private LoginRequest loginRequest;
    private User user;
    private JwtToken jwtToken;

    @BeforeEach
    void setUp () {
        loginRequest = new LoginRequest("test@email.com", "1234567A");

        user = new User();
        user.setId("deee11b9-6db4-4997-9cfc-030f592d3198");
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPassword("$2a$10$41AAmpQjCJPdS6VXgPPFeeXUTI4bOWiu84qJs4wQaEVw6AIE8oYaG");

        jwtToken = new JwtToken("mock-jwt-token");
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando credenciais são válidas")
    void shouldAuthenticateSuccessfully_WhenCredentialsAreValid() {
        // Given
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .thenReturn(true);
        when(detailsService.getTokenAuthenticated(loginRequest.email()))
                .thenReturn(jwtToken);

        // when
        LoginResponse response = authenticationUseCase.authentication(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("deee11b9-6db4-4997-9cfc-030f592d3198");
        assertThat(response.name()).isEqualTo("Test User");
        assertThat(response.email()).isEqualTo("test@email.com");
        assertThat(response.token()).isEqualTo("mock-jwt-token");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(detailsService).getTokenAuthenticated(loginRequest.email());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é encontrado")
    void shouldThrowException_WhenUserNotFound () {
        // Given
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        // when & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationUseCase.authentication(loginRequest));

        assertThat(exception.getMessage()).isEqualTo("E-mail ou senha incorreta");

        verify(userRepository).findByEmail(loginRequest.email());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(detailsService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha é incorreta")
    void shouldThrowException_WhenPasswordIsIncorrect() {
        // GIVEN
        when(userRepository.findByEmail(loginRequest.email()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .thenReturn(false);

        // WHEN / THAT
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationUseCase.authentication(loginRequest) );

        assertThat(exception.getMessage()).isEqualTo("E-mail ou senha incorreta");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(detailsService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando AuthenticationManager falha")
    void shouldThrowException_WhenAuthenticationManagerFails() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // when / that
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationUseCase.authentication(loginRequest));

        assertThat(exception.getMessage()).isEqualTo("E-mail ou senha incorreta");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(detailsService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando geração de token falha")
    void shouldThrowException_WhenTokenGenerationFails() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .thenReturn(true);
        when(detailsService.getTokenAuthenticated(loginRequest.email()))
                .thenThrow(new RuntimeException(("Token generation failed")));

        // when e That
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationUseCase.authentication(loginRequest));

        assertThat(exception.getMessage()).isEqualTo("Token generation failed");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(detailsService).getTokenAuthenticated(loginRequest.email());
    }

    @Test
    @DisplayName("Deve chamar métodos na ordem correta durante autenticação")
    void shouldCallMethodsInCorrectOrder_DuringAuthentication() {
        // Given
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .thenReturn(true);
        when(detailsService.getTokenAuthenticated(loginRequest.email()))
                .thenReturn(jwtToken);

        // when
        authenticationUseCase.authentication(loginRequest);


        // Then - verificar ordem das chamadas
        InOrder inOrder = inOrder(userRepository, passwordEncoder, authenticationManager, detailsService);
        inOrder.verify(userRepository).findByEmail(loginRequest.email());
        inOrder.verify(passwordEncoder).matches(loginRequest.password(), user.getPassword());
        inOrder.verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        inOrder.verify(detailsService).getTokenAuthenticated(loginRequest.email());
    }

    @Test
    @DisplayName("Deve criar UsernamePasswordAuthenticationToken com email e senha corretos")
    void shouldCreateAuthenticationToken_WithCorrectEmailAndPassword() {
        // Given
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .thenReturn(true);
        when(detailsService.getTokenAuthenticated(loginRequest.email()))
                .thenReturn(jwtToken);

        // when
        authenticationUseCase.authentication(loginRequest);

        // Then
        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());

        UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
        assertThat(capturedToken.getPrincipal()).isEqualTo(loginRequest.email());
        assertThat(capturedToken.getCredentials()).isEqualTo(loginRequest.password());
    }
}
