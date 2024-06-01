package blps.entities;

import jakarta.persistence.*;

@Entity
public class CustomerOrder {
  @Id
  @GeneratedValue
  private long id;

  @ManyToOne
  private Customer whose;

  public Customer getCustomer() {
    return whose;
  }

  public long getId() {
    return id;
  }
}
