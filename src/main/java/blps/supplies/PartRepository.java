package blps.supplies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends
    CrudRepository<PartEntity, Long>,
    JpaRepository<PartEntity, Long> {
  List<PartEntity> findByTotal(long total);
  void deleteByMarkedForCleanup(boolean markedForCleanup);
}
