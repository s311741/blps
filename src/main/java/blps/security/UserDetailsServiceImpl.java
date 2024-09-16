package blps.security;

import blps.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import blps.entities.User;
import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepo;

  public UserDetailsServiceImpl(UserRepository userRepo) {
    this.userRepo = userRepo;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    Optional<User> user = userRepo.findByName(name);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }
    return UserDetailsImpl.fromUser(user.get());
  }
}
