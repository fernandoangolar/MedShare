package ao.com.angotech.application.usecases;

import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindUserByEmailUseCase {

    @Autowired
    private UserRepository repository;

    public User execute(String email) {

        return repository.findByEmail(email)
                .orElseThrow( () -> new RuntimeException(String.format("Usuário com Email=%s não encontrado, email")) );
    }
}
