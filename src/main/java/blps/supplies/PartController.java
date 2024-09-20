package blps.supplies;

import blps.ReserveMessage;
import jakarta.persistence.TableGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;
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
  @Transactional
  protected void add(@PathVariable long id, @RequestParam long newlyAvailable) {
    PartEntity part = partRepo.findById(id).get();
    part.addToStock(newlyAvailable);
    partRepo.save(part);
  }

  @PutMapping
  @Transactional
  protected long create(@RequestParam(defaultValue = "0") long initiallyAvailable) {
    if (this.partRepo == null) {
      log.error("Null part repository");
    }
    return partRepo.save(new PartEntity(initiallyAvailable)).getId();
  }

  @KafkaListener(topics = "part")
  @Transactional
  void onOrderMessage(ReserveMessage message) {
    long partId = message.id();
    var maybePart = partRepo.findById(partId);
    if (maybePart.isPresent()) {
      PartEntity part = maybePart.get();
      part.reserveUncommitted(message.reserve());
      part.confirmUncommitted(message.confirm());
      partRepo.save(part);
    } else {
      log.error("Part {} not found", partId);
    }
  }
}