package blps.controllers;

import blps.entities.Part;
import blps.entities.Customer;
import blps.entities.Order;
import blps.exceptions.InvalidPaymentException;
import blps.exceptions.NoPartsAvailableException;
import blps.repositories.PartRepository;
import blps.repositories.OrderRepository;
import blps.repositories.CustomerRepository;
import blps.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/order")
public class OrderController {
  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  private final PartRepository partRepo;
  private final OrderRepository orderRepo;
  private final CustomerRepository customerRepo;
  private final UserRepository userRepo;

  public OrderController(CustomerRepository customerRepo, PartRepository partRepo, OrderRepository orderRepo, UserRepository userRepo) {
    this.partRepo = partRepo;
    this.orderRepo = orderRepo;
    this.customerRepo = customerRepo;
    this.userRepo = userRepo;
  }

  @GetMapping("/{id}")
  private Order get(@PathVariable long id) throws NoSuchElementException {
    return getOrderOfCustomer(getCurrentCustomer(), id);
  }

  @PutMapping
  private long addOrder(@RequestParam long partId) throws NoSuchElementException {
    final Customer customer = getCurrentCustomer();
    final Part part = partRepo.findById(partId).get();

    log.info("Adding order for part {} by {}", partId, customer.getName());

    try {
      part.reserveOne();
    } catch (NoPartsAvailableException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    Order order = new Order(customer, part);
    orderRepo.save(order);
    return order.getId();
  }

  @PutMapping("/{id}/confirm")
  private long confirmOrder(@PathVariable long id, @RequestParam long proofOfPayment) throws NoSuchElementException, InvalidPaymentException {
    Customer customer = getCurrentCustomer();
    Order order = getOrderOfCustomer(customer, id);
    Part part = order.getPart();
    log.info("Confirming order {} by {} on part {}", order.getId(), customer.getName(), part.getId());
    order.verifyPayment(proofOfPayment);
    orderRepo.delete(order);

    part.confirmSale();
    partRepo.save(part);
    return part.getId();
  }

  @DeleteMapping("/{id}")
  private void cancelOrder(@PathVariable long id) throws NoSuchElementException {
    Customer customer = getCurrentCustomer();
    Order order = getOrderOfCustomer(customer, id);
    log.info("Cancelling order {} by {}", id, customer.getName());
    orderRepo.delete(order);
  }

  private Customer getCurrentCustomer() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    var maybeUser = userRepo.findByName(auth.getName());
    if (maybeUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a user???");
    }

    var maybeCustomer = customerRepo.findByUser(maybeUser.get());
    if (maybeCustomer.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a customer");
    }
    return maybeCustomer.get();
  }

  private Order getOrderOfCustomer(Customer customer, long orderId) {
    Order order = orderRepo.findById(orderId).get();
    Customer owner = order.getCustomer();

    if (owner.getId() != customer.getId()) {
      log.info("Customer {} was trying to access order {}, which belongs to customer {} instead", customer.getName(), order.getId(), owner.getName());
      // Other customers don't need know whether the order even existretend not
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return order;
  }
}