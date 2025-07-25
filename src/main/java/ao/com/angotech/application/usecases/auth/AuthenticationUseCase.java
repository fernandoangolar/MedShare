package ao.com.angotech.application.usecases.auth;

import ao.com.angotech.application.dtos.auth.LoginRequest;
import ao.com.angotech.application.dtos.auth.LoginResponse;
import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.repository.UserRepository;
import ao.com.angotech.infrastructure.jwt.JwtToken;
import ao.com.angotech.infrastructure.jwt.JwtUserDetailsService;
import ao.com.angotech.infrastructure.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUseCase {

    private static  final Logger logger = LoggerFactory.getLogger(AuthenticationUseCase.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService detailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public LoginResponse authentication(LoginRequest request) {

        logger.info("Iniciando processo de autenticação para email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow( () -> new RuntimeException("E-mail ou senha incorreta") );

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("E-mail ou senha incorreta");
        }

        try {

            // Criar token de autenticação
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(request.email(), request.password());

            // Autentica
            authenticationManager.authenticate(authenticationToken);

            // Gerar JWT token
            JwtToken jwtToken = detailsService.getTokenAuthenticated(request.email());

            // Manter resposta
            return new LoginResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    jwtToken.getToken() // Ou o metodo apropriado para obter o token
            );

        } catch ( AuthenticationException ex ) {
            logger.error("Erro na autenticação para email '{}': {}", request.email(), ex.getMessage());
            throw new RuntimeException("E-mail ou senha incorreta");
        }

    }
}
