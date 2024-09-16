package blps.controllers;

import blps.entities.Order;
import blps.entities.Part;
import blps.repositories.OrderRepository;
import blps.repositories.PartRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/part")
public class PartController {
  private final PartRepository partRepo;
  private final OrderRepository orderRepository;

  public PartController(PartRepository partRepo, OrderRepository orderRepository) {
    this.partRepo = partRepo;
    this.orderRepository = orderRepository;
  }

  @GetMapping("/{id}/available")
  private long getAvailable(@PathVariable long id) {
    return partRepo.findById(id).map(Part::getAvailable).orElse(0L);
  }

  @GetMapping("/{id}")
  private Part get(@PathVariable long id) {
    return partRepo.findById(id).get();
  }

  @PutMapping("/{id}/add")
  private void add(@PathVariable long id, @RequestParam long newlyAvailable) {
    partRepo.findById(id).get().addToStock(newlyAvailable);
  }

  @PutMapping
  private long create(@RequestParam(defaultValue = "0") long initiallyAvailable) {
    return partRepo.save(new Part(initiallyAvailable)).getId();
  }

  @DeleteMapping("/{id}")
  private void retire(@PathVariable long id) {
    Part part = partRepo.findById(id).get();
    long available = part.getTotal();
    if (available != 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Cannot retire car part with id [%d] when there are still %d of them present", id, available));
    }
    for (Order order : orderRepository.findAll()) {
      if (order.getPart().getId() == id) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Cannot retire car part while stray order from customer %s exists on it", order.getCustomerName()));
      }
    }
    partRepo.delete(part);
  }
}