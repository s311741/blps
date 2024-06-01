package blps.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Entity
public class Customer {
  @Id
  @GeneratedValue
  private long id;

  private String displayname;

  @OneToMany
  private Set<CustomerOrder> ordersInFlight = new HashSet<>();

  public Customer(String displayname) {
    this.displayname = displayname;
  }

  public Customer() {
    this("");
  }

  public Set<CustomerOrder> getOrdersInFlight() {
    return ordersInFlight;
  }

  public void removeOrder(CustomerOrder order) {
    if (!ordersInFlight.remove(order)) {
      throw new NoSuchElementException("Customer has no such order");
    }
  }
}