package ao.com.angotech.application.dtos.auth;

import ao.com.angotech.domain.enuns.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest (

        @NotBlank(message = "O campo nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve conter entre 2 a 100 Caracteres")
        String name,

        @NotBlank(message = "O campo email é obrigatório" )
        @Email(message = "Formato do e-mail está invalido", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
        String email,

        @NotBlank(message = "O campo email é obrigatório" )
        @Size(min = 6, max = 8, message = "Senha deve ter no minimo 6 caracteres e no máximo 8")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "Senha deve cinter oelo menos letra maiúscula e um número")
        String password,

        @NotNull(message = "O campo role é obrigatório")
        String role

//        @NotNull(message = "O campo role é obrigatório")
//        Role role
) { }
