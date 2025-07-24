package ao.com.angotech.application.controllers;

import ao.com.angotech.application.dtos.auth.RegisterRequest;
import ao.com.angotech.application.dtos.auth.RegisterResponse;
import ao.com.angotech.application.usecases.auth.RegisterUserUseCase;
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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> create(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registerUserUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

}
