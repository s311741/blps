package blps.entities;

import blps.exceptions.InvalidPaymentException;
import jakarta.persistence.*;

@Entity(name = "customer_order")
public class Order {
  @Id
  @GeneratedValue
  private long id;

  @ManyToOne
  private Customer whose;

  @ManyToOne
  Part part;

  public Order() {
  }

  public Order(Customer whose, Part part) {
    this.whose = whose;
    this.part = part;
  }

  public Customer getCustomer() {
    return whose;
  }

  public long getId() {
    return id;
  }

  public Part getPart() {
    return part;
  }

  public void verifyPayment(long proofOfPayment) throws InvalidPaymentException {
    // Just some logic that this order was really paid for...let's say that nonpositive proofs are invalid
    if (proofOfPayment <= 0) {
      throw new InvalidPaymentException();
    }
    // OK, payment is good
  }
}
