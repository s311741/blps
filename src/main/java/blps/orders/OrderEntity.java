package blps.orders;

import blps.supplies.PartEntity;
import jakarta.persistence.*;

@Entity(name = "customer_order")
public class OrderEntity {
  @Id
  @GeneratedValue
  private long id;

  private String whose;
  long partId;

  public OrderEntity() {
  }

  public OrderEntity(String whose, long partId) {
    this.whose = whose;
    this.partId = partId;
  }

  public String getCustomerName() {
    return whose;
  }

  public long getId() {
    return id;
  }

  public long getPartId() {
    return partId;
  }

  public void verifyPayment(long proofOfPayment) throws InvalidPaymentException {
    // Just some logic that this order was really paid for...let's say that nonpositive proofs are invalid
    if (proofOfPayment <= 0) {
      throw new InvalidPaymentException();
    }
    // OK, payment is good
  }
}
