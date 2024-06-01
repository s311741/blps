package blps.controllers;

import blps.entities.Customer;
import blps.repositories.CarPartRepository;
import blps.repositories.CustomerOrderRepository;
import blps.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
public class PublicController {
  private static final Logger log = LoggerFactory.getLogger(PublicController.class);
  private final CustomerRepository customerRepo;
  private final CarPartRepository carPartRepo;
  private final CustomerOrderRepository customerOrderRepo;

  public PublicController(CustomerRepository customerRepo, CarPartRepository carPartRepo, CustomerOrderRepository customerOrderRepo) {
    this.customerRepo = customerRepo;
    this.carPartRepo = carPartRepo;
    this.customerOrderRepo = customerOrderRepo;
  }

  // TODO: There is only 1 customer now
  private Customer getTheCustomer() {
    return customerRepo.getById(0L);
  }

  @PostMapping("/order/create/{partId}")
  private Long addOrder(@PathVariable long partId) {
    final long result = 2L;
    log.info("Adding order {} for part {}", result, partId);
    final Customer customer = getTheCustomer();
    return result;
  }

  @DeleteMapping("/order/remove/{orderId}")
  private void removeOrder(@PathVariable long orderId) {
    log.info("Removing order {}", orderId);
    getTheCustomer().removeOrder(customerOrderRepo.findById(orderId).get());
  }

  @ExceptionHandler(NoSuchElementException.class)
  private ResponseEntity<Object> notFound(NoSuchElementException ex) {
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}