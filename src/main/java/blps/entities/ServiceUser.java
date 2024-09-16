package blps.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name = "users")
public class ServiceUser {
  @Id
  @GeneratedValue
  private long id;

  private String name;

  @ManyToOne
  private Role role;

  public ServiceUser(String name, Role role) {
    this.name = name;
    this.role = role;
  }

  public ServiceUser() {
  }

  public String getName() {
    return name;
  }

  public long getId() {
    return id;
  }

  public Role getRole() {
    return role;
  }
}
