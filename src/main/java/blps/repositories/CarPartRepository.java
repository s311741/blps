package blps.repositories;

import blps.entities.CarPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CarPartRepository extends
    CrudRepository<CarPart, Long>,
    JpaRepository<CarPart, Long> {
}
