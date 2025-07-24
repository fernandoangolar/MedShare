package ao.com.angotech.domain.repository;

import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.enuns.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.role FROM User u WHERE u.email = :email")
    Role findRoleByEmail(String email);
}
