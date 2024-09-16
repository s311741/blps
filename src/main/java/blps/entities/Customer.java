package blps.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Customer {
  @Id
  private long id;

  @OneToOne(optional = true)
  private User user;

  @OneToMany
  private Set<Order> ordersInFlight = new HashSet<>();

  public Customer() {
  }

  public Customer(User user) {
    this.user = user;
  }

  public long getId() {
    return id;
  }

  public Set<Order> getOrdersInFlight() {
    return ordersInFlight;
  }

  public String getName() {
    return user.getName();
  }
}