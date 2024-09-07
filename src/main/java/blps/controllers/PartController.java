package blps.controllers;

import blps.entities.Order;
import blps.entities.Part;
import blps.exceptions.NoPartsAvailableException;
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

  @GetMapping("/available")
  private long getAvailable(@RequestParam long id) {
    return partRepo.findById(id).map(Part::getAvailable).orElse(0L);
  }

  @GetMapping
  private Part get(@RequestParam long id) {
    return partRepo.findById(id).get();
  }

  @PutMapping("/add")
  private void add(@RequestParam long id, @RequestParam long newlyAvailable) {
    partRepo.findById(id).get().addToStock(newlyAvailable);
  }

  @PutMapping
  private long create(@RequestParam(defaultValue = "0") long initiallyAvailable) {
    return partRepo.save(new Part(initiallyAvailable)).getId();
  }

  @DeleteMapping
  private void retire(@RequestParam long id) {
    Part part = partRepo.findById(id).get();
    synchronized (part) {
      long available = part.getTotal();
      if (available != 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Cannot retire car part with id [%d] when there are still %d of them present", id, available));
      }
      for (Order order : orderRepository.findAll()) {
        if (order.getPart().getId() == id) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Cannot retire car part while stray order from customer %s exists on it", order.getCustomer().getDisplayName()));
        }
      }
      partRepo.delete(part);
    }
  }
}