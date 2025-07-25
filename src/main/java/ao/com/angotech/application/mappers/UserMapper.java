package ao.com.angotech.application.mappers;

import ao.com.angotech.application.dtos.auth.RegisterRequest;
import ao.com.angotech.application.dtos.auth.RegisterResponse;
import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.enuns.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(RegisterRequest request) {

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        // user.setRole(request.role()); Quando a role vem do tipo Role (Enum) nos dtos
        user.setRole(Role.valueOf(request.role().toUpperCase())); // Quando a role e uma String ns dtos

        return user;
    }

    public RegisterResponse toResponse(User user) {

        return new RegisterResponse(
                user.getName(),
                user.getEmail()
        );
    }
}
