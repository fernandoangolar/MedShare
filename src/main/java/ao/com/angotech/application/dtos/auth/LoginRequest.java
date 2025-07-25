package ao.com.angotech.application.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "O campo email é obrigatório" )
        @Email(message = "Formato do e-mail está invalido", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
        String email,

        @NotBlank(message = "O campo password é obrigatório" )
        @Size(min = 6, max = 8, message = "Senha deve ter no minimo 6 caracteres e no máximo 8")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "Senha deve ter pelo menos uma letra maiúscula e um número")
        String password

) {
}
