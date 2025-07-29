package ao.com.angotech.angotech.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ao.com.angotech.application.dtos.auth.RegisterRequest;
import ao.com.angotech.application.dtos.auth.RegisterResponse;
import ao.com.angotech.application.mappers.UserMapper;
import ao.com.angotech.application.usecases.auth.RegisterUserUseCase;
import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RegisterUserUseCase userUseCase;

    private User user;
    private RegisterRequest request;
    private RegisterResponse response;

    @BeforeEach
    public void setup() {
        request = new RegisterRequest(
                "Fernando",
                "fernando@gmail.com",
                "senha1",
                "USER"
        );
        user = new User();

        response = new RegisterResponse("", "Fernando", "fernando@gmail.com");
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("$2a$12$G/oQ/lmt3f.mtY8GgE9Gq.HhVkJkVXgjaTnpUNhQEQGhlDTe9jNpK");
        when(repository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        RegisterResponse resultado = userUseCase.execute(request);

        assertNotNull(resultado);
        assertEquals(response, resultado);

        verify(repository).existsByEmail(request.email());
        verify(userMapper).toUser(request);
        verify(passwordEncoder).encode(request.password());
        verify(repository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(repository.existsByEmail(request.email())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userUseCase.execute(request));
        assertEquals("Já existe um usuário com este email", exception.getMessage());

        verify(repository).existsByEmail(request.email());
        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userMapper, never()).toUser(any());
    }

    @Test
    void deveChamarMapperParaConverterRequestEmUser() {
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("$2a$12$G/oQ/lmt3f.mtY8GgE9Gq.HhVkJkVXgjaTnpUNhQEQGhlDTe9jNpK");
        when(repository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userUseCase.execute(request);

        verify(userMapper).toUser(request);
    }

    @Test
    void deveChamarPasswordEncoderParaCodificarSenha() {
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("$2a$12$G/oQ/lmt3f.mtY8GgE9Gq.HhVkJkVXgjaTnpUNhQEQGhlDTe9jNpK");
        when(repository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userUseCase.execute(request);

        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void deveChamarRepositoryParaSalvarUsuario() {
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("$2a$12$G/oQ/lmt3f.mtY8GgE9Gq.HhVkJkVXgjaTnpUNhQEQGhlDTe9jNpK");
        when(repository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        userUseCase.execute(request);

        verify(userMapper).toUser(request);
    }
}
