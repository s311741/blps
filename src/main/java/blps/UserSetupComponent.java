package blps;

import blps.entities.Customer;
import blps.entities.Role;
import blps.entities.ServiceUser;
import blps.repositories.CustomerRepository;
import blps.repositories.RoleRepository;
import blps.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserSetupComponent implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(UserSetupComponent.class);

  private final RoleRepository roleRepo;
  private final UserRepository userRepo;
  private final CustomerRepository customerRepo;

  private boolean hasAlreadyRun = false;

  public UserSetupComponent(RoleRepository roleRepo, UserRepository userRepo, CustomerRepository customerRepo) {
    this.roleRepo = roleRepo;
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
  }

  // Just pre-fill users to have somebody to work with. "Example" code.

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    fillIfNeeded();
  }

  public Collection<UserDetails> getAllUserDetails() {
    fillIfNeeded();
    return userRepo.findAll().stream().map(
        user -> User.withDefaultPasswordEncoder()
            .username(user.getName())
            .password("password")
            .roles(user.getRole().getName())
            .build()
    ).collect(Collectors.toList());
  }

  private void fillIfNeeded() {
    if (!hasAlreadyRun) {
      Role sellerRole = ensureRoleExists("SELLER");
      Role customerRole = ensureRoleExists("CUSTOMER");
      Role supplierRole = ensureRoleExists("SUPPLIER");
      ensureUserExists("ourselves", sellerRole);
      ensureUserExists("some_customer", customerRole);
      ensureUserExists("our_supplier", supplierRole);
      hasAlreadyRun = true;
    }
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
      log.info("ServiceUser {} did not exist - creating", name);
      ServiceUser user = new ServiceUser(name, role);
      userRepo.save(user);

      if (role.getName().equals("CUSTOMER")) {
        log.info("{} is also a customer - creating", name);
        customerRepo.save(new Customer(user));
      }
    }
  }
}
