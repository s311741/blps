package blps;

import blps.entities.Customer;
import blps.entities.Role;
import blps.entities.User;
import blps.repositories.CustomerRepository;
import blps.repositories.RoleRepository;
import blps.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserSetupComponent implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(UserSetupComponent.class);

  private final RoleRepository roleRepo;
  private final UserRepository userRepo;
  private final CustomerRepository customerRepo;

  public UserSetupComponent(RoleRepository roleRepo, UserRepository userRepo, CustomerRepository customerRepo) {
    this.roleRepo = roleRepo;
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
  }

  // Just pre-fill users to have somebody to work with. "Example" code.

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    Role sellerRole = ensureRoleExists("ROLE_SELLER");
    Role customerRole = ensureRoleExists("ROLE_CUSTOMER");
    Role supplierRole = ensureRoleExists("ROLE_SUPPLIER");
    ensureUserExists("ourselves", sellerRole);
    ensureUserExists("some_customer", customerRole);
    ensureUserExists("our_supplier", supplierRole);
  }

  protected Role ensureRoleExists(String name) {
    Role role = roleRepo.findById(name).orElse(null);
    if (role == null) {
      log.info("Role {} did not exist - creating", name);
      role = new Role(name);
      roleRepo.save(role);
    }
    return role;
  }

  protected void ensureUserExists(String name, Role role) {
    if (userRepo.findByName(name).isEmpty()) {
      log.info("User {} did not exist - creating", name);
      User user = new User(name, role);
      userRepo.save(user);

      if (role.getName().equals("ROLE_CUSTOMER")) {
        log.info("{} is also a customer - creating", name);
        customerRepo.save(new Customer(user));
      }
    }
  }
}
