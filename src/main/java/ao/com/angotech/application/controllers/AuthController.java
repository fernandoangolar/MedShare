package ao.com.angotech.application.controllers;

import ao.com.angotech.application.dtos.auth.RegisterRequest;
import ao.com.angotech.application.dtos.auth.RegisterResponse;
import ao.com.angotech.application.usecases.auth.RegisterUserUseCase;
import ao.com.angotech.infrastructure.dto.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

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
