package es.meep.demo.repository;

import es.meep.demo.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * JPA Repository for vehicle's model
 */
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    /**
     * Returns all vehicles whose id is not present in the given list of identifiers
     *
     * @param ids the list of identifiers to be matched
     * @return the list of vehicles whose id is not present in the given list of identifiers
     */
    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN (:ids)")
    List<Vehicle> getUnavailable(@Param("ids") List<String> ids);
}
