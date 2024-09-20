package blps.supplies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PartRepository extends
    CrudRepository<PartEntity, Long>,
    JpaRepository<PartEntity, Long> {
}
