package blps.controllers;

import blps.entities.Order;
import blps.entities.Part;
import blps.exceptions.InvalidPaymentException;
import blps.exceptions.NoPartsAvailableException;
import blps.repositories.OrderRepository;
import blps.repositories.PartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

  public OrderController(PartRepository partRepo, OrderRepository orderRepo) {
    this.partRepo = partRepo;
    this.orderRepo = orderRepo;
  }

  @GetMapping("/{id}")
  private Order get(@PathVariable long id) throws NoSuchElementException {
    return getOrderOfCustomer(getCurrentCustomerName(), id);
  }

  @PutMapping
  private long addOrder(@RequestParam long partId) throws NoSuchElementException {
    final String customerName = getCurrentCustomerName();
    final Part part = partRepo.findById(partId).get();

    log.info("Adding order for part {} by {}", partId, customerName);

    try {
      part.reserveOne();
    } catch (NoPartsAvailableException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    Order order = new Order(customerName, part);
    orderRepo.save(order);
    return order.getId();
  }

  @PutMapping("/{id}/confirm")
  private long confirmOrder(@PathVariable long id, @RequestParam long proofOfPayment) throws NoSuchElementException, InvalidPaymentException {
    String customerName = getCurrentCustomerName();
    Order order = getOrderOfCustomer(customerName, id);
    Part part = order.getPart();
    log.info("Confirming order {} by {} on part {}", order.getId(), customerName, part.getId());
    order.verifyPayment(proofOfPayment);
    orderRepo.delete(order);

    part.confirmSale();
    partRepo.save(part);
    return part.getId();
  }

  @DeleteMapping("/{id}")
  private void cancelOrder(@PathVariable long id) throws NoSuchElementException {
    String customerName = getCurrentCustomerName();
    Order order = getOrderOfCustomer(customerName, id);
    log.info("Cancelling order {} by {}", id, customerName);
    orderRepo.delete(order);
  }

  private static final GrantedAuthority CUSTOMER_ROLE = new SimpleGrantedAuthority("ROLE_CUSTOMER");

  private String getCurrentCustomerName() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!auth.getAuthorities().contains(CUSTOMER_ROLE)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a customer");
    }
    return auth.getName();
  }

  private Order getOrderOfCustomer(String customerName, long orderId) {
    Order order = orderRepo.findById(orderId).get();
    String ownerName = order.getCustomerName();
    if (!ownerName.equals(customerName)) {
      log.info("Customer {} was trying to access order {}, which belongs to customer {} instead", customerName, order.getId(), ownerName);
      // Other customers don't need know whether the order even existretend not
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return order;
  }
}