package blps.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class CarPart {
  @Id
  @GeneratedValue
  long id;

  AtomicInteger available = new AtomicInteger(0);
}