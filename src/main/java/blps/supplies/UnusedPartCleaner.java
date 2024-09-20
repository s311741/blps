package blps.supplies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class UnusedPartCleaner {
  private static final Logger log = LoggerFactory.getLogger(UnusedPartCleaner.class);
  private final PartRepository partRepo;

  public UnusedPartCleaner(PartRepository partRepo) {
    this.partRepo = partRepo;
  }

  @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 10)
  @Transactional
  public void cleanup() {
    partRepo.deleteByMarkedForCleanup(true);

    List<PartEntity> withNoneLeft = partRepo.findByTotal(0);
    log.info("With none left: {}", withNoneLeft.size());
    for (PartEntity part : withNoneLeft) {
      part.markForCleanup();
    }
    partRepo.saveAll(withNoneLeft);
  }
}
