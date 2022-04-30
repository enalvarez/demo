package es.meep.demo.repository;

import es.meep.demo.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repository for vehicle's model
 */
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
