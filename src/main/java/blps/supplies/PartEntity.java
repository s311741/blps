package blps.supplies;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Entity(name = "part")
@Transactional(propagation = Propagation.MANDATORY)
public class PartEntity {
  @Id
  @GeneratedValue
  private long id;

  private long total = 0;
  private long reserved = 0;

  public PartEntity() {
  }

  public PartEntity(long initiallyAvailable) {
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

  public void reserveUncommitted(long n) {
    reserved += n;
    if (reserved > total) {
      reserved = total;
    }
    if (reserved < 0) {
      reserved = 0;
    }
  }

  public void addToStock(long number) {
    total += number;
  }

  public void confirmUncommitted(long n) {
    total -= n;
    reserved -= n;
  }
}