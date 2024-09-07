package blps.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Customer {
  @Id
  private String id;

  private String displayName;

  @OneToMany
  private Set<Order> ordersInFlight = new HashSet<>();

  public String getId() {
    return id;
  }

  public Customer(String displayName) {
    this.displayName = displayName;
  }

  public Customer() {
    this("");
  }

  public Set<Order> getOrdersInFlight() {
    return ordersInFlight;
  }

  public String getDisplayName() {
    return displayName;
  }
}