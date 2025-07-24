package ao.com.angotech.infrastructure.jwt;

import ao.com.angotech.application.usecases.FindRoleByEmailUseCase;
import ao.com.angotech.application.usecases.FindUserByEmailUseCase;
import ao.com.angotech.domain.entitiy.User;
import ao.com.angotech.domain.enuns.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private FindUserByEmailUseCase findUserByEmailUseCase;

    @Autowired
    private FindRoleByEmailUseCase findRoleByEmailUseCase;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUserByEmailUseCase.execute(email);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String email) {
        Role role = findRoleByEmailUseCase.execute(email);
        return JwtUtils.createToken(email, role.name().substring("Role_".length()));
    }
}
