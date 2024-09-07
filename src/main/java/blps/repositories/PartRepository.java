package blps.repositories;

import blps.entities.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PartRepository extends
    CrudRepository<Part, Long>,
    JpaRepository<Part, Long> {
}
