package blps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import blps.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByName(String name);
}
