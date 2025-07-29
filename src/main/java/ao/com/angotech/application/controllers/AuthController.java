package ao.com.angotech.application.controllers;

import ao.com.angotech.application.dtos.auth.LoginRequest;
import ao.com.angotech.application.dtos.auth.LoginResponse;
import ao.com.angotech.application.dtos.auth.RegisterRequest;
import ao.com.angotech.application.dtos.auth.RegisterResponse;
import ao.com.angotech.application.usecases.auth.AuthenticationUseCase;
import ao.com.angotech.application.usecases.auth.RegisterUserUseCase;
import ao.com.angotech.infrastructure.dto.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private AuthenticationUseCase authenticationUseCase;

    @Operation(summary = "Autenticar na API", description = "Recurso de autenticação na API",
        responses = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso e retorno de um bearer token",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
            ),
                @ApiResponse(responseCode = "400", description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                ),
                @ApiResponse(responseCode = "422", description = "Compo(s) Inválido(s)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                ),
                @ApiResponse(responseCode = "500", description = "Interbal server error",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                )
        }
    )
    @PostMapping("/auth")
    public ResponseEntity<?> authentication(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        logger.info("Processo de autenticação pelo login {}", request.email());

        try {
            LoginResponse response = authenticationUseCase.authentication(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);
        } catch (AuthenticationException exception) {
            logger.info("Bad Credentials from email '{}'", request.email());
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorMessage(httpRequest, HttpStatus.BAD_REQUEST, "Credenciais Inválidas"));
        } catch (RuntimeException exception) {
            logger.warn("Authentication error for email '{}': {}", request.email(), exception.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorMessage(httpRequest, HttpStatus.BAD_REQUEST, exception.getMessage()));
        }
    }

    @Operation(summary = "Create a new user", description = "Create a new user in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successsfully",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RegisterResponse.class)
                )
            ),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class)
                )
            ),
            @ApiResponse(responseCode = "500", description = "Interal server error",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorMessage.class)
                )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> create(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registerUserUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

}
