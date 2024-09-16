package blps.entities;

import blps.exceptions.NoPartsAvailableException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Entity
@Transactional(propagation = Propagation.MANDATORY)
public class Part {
  @Id
  @GeneratedValue
  private long id;

  private long total = 0;
  private long reserved = 0;

  public Part() {
  }

  public Part(long initiallyAvailable) {
    this.total = initiallyAvailable;
  }

  public long getId() {
    return id;
  }

  public long getAvailable() {
    return total - reserved;
  }

  public long getTotal() {
    return total;
  }

  public void reserveOneUncommitted() throws NoPartsAvailableException {
    if (reserved == total) {
      throw new NoPartsAvailableException(getId());
    }
    ++reserved;
  }

  public void unreserveOneUncommitted() {
    if (reserved > 0) {
      --reserved;
    }
  }

  public void addToStock(long number) {
    total += number;
  }

  public void confirmSaleUncommitted() {
    if (reserved == 0) {
      throw new NoSuchElementException("This part was never reserved, or sale already confirmed");
    }
    --total;
    --reserved;
  }
}