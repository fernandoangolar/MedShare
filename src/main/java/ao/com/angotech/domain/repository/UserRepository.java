package ao.com.angotech.domain.repository;

import ao.com.angotech.domain.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
