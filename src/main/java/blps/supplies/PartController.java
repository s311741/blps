package blps.supplies;

import blps.ReserveMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/part")
public class PartController {
  private static final Logger log = LoggerFactory.getLogger(PartController.class);
  private final PartRepository partRepo;

  public PartController(PartRepository partRepo) {
    this.partRepo = partRepo;
  }

  @GetMapping("/{id}/available")
  private long getAvailable(@PathVariable long id) {
    return partRepo.findById(id).map(PartEntity::getAvailable).orElse(0L);
  }

  @GetMapping("/{id}")
  private PartEntity get(@PathVariable long id) {
    return partRepo.findById(id).get();
  }

  @PutMapping("/{id}/add")
  private void add(@PathVariable long id, @RequestParam long newlyAvailable) {
    partRepo.findById(id).get().addToStock(newlyAvailable);
  }

  @PutMapping
  private long create(@RequestParam(defaultValue = "0") long initiallyAvailable) {
    return partRepo.save(new PartEntity(initiallyAvailable)).getId();
  }

  @KafkaListener(topics = "part")
  void onOrderMessage(ReserveMessage message) {
    log.info("Received Kafka message {}", message);
  }
}