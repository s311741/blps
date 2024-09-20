package blps.orders;

import blps.ReserveMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/order")
public class OrderController {
  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  private final OrderRepository orderRepo;
  private final KafkaTemplate<String, ReserveMessage> partTemplate;

  public OrderController(OrderRepository orderRepo, KafkaTemplate<String, ReserveMessage> partTemplate) {
    this.orderRepo = orderRepo;
    this.partTemplate = partTemplate;
  }

  @GetMapping("/{id}")
  @Transactional
  protected OrderEntity get(@PathVariable long id) throws NoSuchElementException {
    return getOrderOfCustomer(getCurrentCustomerName(), id);
  }

  @GetMapping("/testKafka")
  protected void testKafka() {
    partTemplate.send("part", new ReserveMessage(-1, -2)).thenAccept(sendResult -> log.info("Send confirmed!"));
    log.info("Sent!");
  }

  @PutMapping
  @Transactional
  protected long addOrder(@RequestParam long partId) throws NoSuchElementException {
    return 0;
  }

  @PutMapping("/{id}/confirm")
  @Transactional
  protected long confirmOrder(@PathVariable long id, @RequestParam long proofOfPayment) throws NoSuchElementException, InvalidPaymentException {
    return 0;
  }

  @PreAuthorize("hasRole('SELLER')")
  @PostMapping("/{id}/deliver")
  @Transactional
  protected void deliverOrder(@PathVariable long id) throws NoSuchElementException {
    orderRepo.deleteById(id);
  }

  @DeleteMapping("/{id}")
  @Transactional
  protected void cancelOrder(@PathVariable long id) throws NoSuchElementException {
  }

  private static final GrantedAuthority CUSTOMER_ROLE = new SimpleGrantedAuthority("ROLE_CUSTOMER");

  private String getCurrentCustomerName() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!auth.getAuthorities().contains(CUSTOMER_ROLE)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not a customer");
    }
    return auth.getName();
  }

  @Transactional
  protected OrderEntity getOrderOfCustomer(String customerName, long orderId) {
    OrderEntity order = orderRepo.findById(orderId).get();
    String ownerName = order.getCustomerName();
    if (!ownerName.equals(customerName)) {
      log.info("Customer {} was trying to access order {}, which belongs to customer {} instead", customerName, order.getId(), ownerName);
      // Other customers don't need know whether the order even exists, pretend not
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return order;
  }
}