package ao.com.angotech.application.usecases.auth;

import ao.com.angotech.application.dtos.auth.RegisterRequest;
import ao.com.angotech.application.dtos.auth.RegisterResponse;
import ao.com.angotech.application.mappers.UserMapper;
import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCase {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse execute(RegisterRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Já existe um usuário com este email");
        }

        User user = UserMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.password()));
        user = repository.save(user);

        return UserMapper.toResponse(user);
    }
}
