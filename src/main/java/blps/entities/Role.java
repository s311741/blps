package blps.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "roles")
public class Role {
  @Id
  private String name;

  public Role(String name) {
    this.name = name;
  }

  public Role() {
  }

  public String getName() {
    return name;
  }
}