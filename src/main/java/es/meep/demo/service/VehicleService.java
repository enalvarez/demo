package es.meep.demo.service;

import es.meep.demo.config.MeepDevProperties;
import es.meep.demo.domain.Vehicle;
import es.meep.demo.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains a scheduled method to invoke Meep's dev server every 30 seconds to update the vehicles info
 */
@Slf4j
@Service
public class VehicleService {
    /**
     * Container of properties required to connect to Meep's dev server
     */
    private final MeepDevProperties meepDevProperties;
    /**
     * The instance of the vehicle repository
     */
    private final VehicleRepository vehicleRepository;

    /**
     * Creates a new instance with the required dependencies
     *
     * @param meepDevProperties the properties needed to connect to Meep's dev server
     * @param vehicleRepository the instance of the vehicle repository
     */
    @Autowired
    public VehicleService(MeepDevProperties meepDevProperties, VehicleRepository vehicleRepository) {
        this.meepDevProperties = meepDevProperties;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Checks the availability of vehicles every 30 seconds
     */
    @Scheduled(fixedDelay = 30000L, initialDelay = 5000L)
    public void checkAvailability() {
        // get the vehicles info
        List<Vehicle> vehicles = getVehicles();
        // only if the list is not empty
        if (!vehicles.isEmpty()) {
            // create a container to store stats about changes
            Map<String, Integer> stats = new HashMap<>();
            // first, we need to get all existing vehicles to update them
            List<Vehicle> existingVehicles = vehicles.stream()
                    .filter(v -> vehicleRepository.existsById(v.getId()))
                    .collect(Collectors.toList());
            // if there are vehicles to be updated
            if (!existingVehicles.isEmpty()) {
                // update the stats and update the existing vehicles
                stats.put("Updated", existingVehicles.size());
                vehicleRepository.saveAll(existingVehicles);
            }

            // next, we need to get all new available vehicles, so copy the original list
            List<Vehicle> newVehiclesAvailable = new ArrayList<>(vehicles);
            // and remove all existing vehicles from it
            newVehiclesAvailable.removeAll(existingVehicles);
            // if there are new vehicles available
            if (!newVehiclesAvailable.isEmpty()) {
                // update the stats and store the new vehicles
                stats.put("New", newVehiclesAvailable.size());
                vehicleRepository.saveAll(newVehiclesAvailable);
            }

            // and finally, we need to check vehicles that aren't available right now
            List<Vehicle> vehiclesUnavailable = vehicleRepository.getUnavailable(vehicles.parallelStream().map(Vehicle::getId).collect(Collectors.toList()));
            // if there are unavailable vehicles
            if (!vehiclesUnavailable.isEmpty()) {
                // update the stats and remove the unavailable vehicles
                stats.put("Unavailable", vehiclesUnavailable.size());
                vehicleRepository.deleteAll(vehiclesUnavailable);
            }

            // Create a trace including all stats
            StringBuilder sb = new StringBuilder("\n#############################");
            sb.append("\nAvailable vehicles: ").append(vehicleRepository.count());
            stats.forEach((key, value) -> sb.append("\n\t * ").append(key).append(": ").append(value));
            sb.append("\n#############################");
            // log the stats
            log.info(sb.toString());
        } else {
            log.warn("The list of vehicles is empty. Do nothing");
        }
    }

    /**
     * Invokes a remote service to obtain a list of vehicles
     *
     * @return the list of vehicles from the remote service.
     * If the service cannot be reached, a fallback list is returned
     */
    private List<Vehicle> getVehicles() {
        log.info("##### Obtaining info from: {}", meepDevProperties.getFullUrl());
        // create a web client instance to obtain the vehicles info
        return WebClient.create()
                .get()
                .uri(meepDevProperties.getFullUrl())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Vehicle.class)
                .collectList()
                .onErrorResume(ex -> {
                    log.warn("Cannot obtain vehicles info server responds with [{}]", ex.getMessage());
                    return Mono.just(Collections.emptyList());
                })
                .block();
    }
}
