package blps.repositories;

import blps.entities.ServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ServiceUser, Long> {
  Optional<ServiceUser> findByName(String name);
}
