package blps.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name = "users")
public class User {
  @Id
  @GeneratedValue
  private long id;

  private String name;

  @ManyToOne
  private Role role;

  public User(String name, Role role) {
    this.name = name;
    this.role = role;
  }

  public User() {
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
