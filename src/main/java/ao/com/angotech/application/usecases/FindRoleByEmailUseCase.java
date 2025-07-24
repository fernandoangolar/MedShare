package ao.com.angotech.application.usecases;

import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.enuns.Role;
import ao.com.angotech.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindRoleByEmailUseCase {

    @Autowired
    private UserRepository repository;

    public Role execute(String email) {
        return repository.findRoleByEmail(email);
    }
}
