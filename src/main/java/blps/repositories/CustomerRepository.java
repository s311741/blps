package blps.repositories;

import blps.entities.Customer;
import blps.entities.ServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
  Optional<Customer> findByUser(ServiceUser user);
}