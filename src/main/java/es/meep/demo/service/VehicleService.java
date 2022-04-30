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

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            // first, we need to get all existing vehicles to update them
            List<Vehicle> existingVehicles = vehicles.stream()
                    .filter(v -> vehicleRepository.existsById(v.getId()))
                    .collect(Collectors.toList());
            // if there are new vehicles available, store them
            if (!existingVehicles.isEmpty()) {
                log.info("Updating info of {} vehicles", existingVehicles.size());
                vehicleRepository.saveAll(existingVehicles);
            }

            // next, we need to get all new available vehicles
            List<Vehicle> newVehiclesAvailable = vehicles.stream()
                    .filter(v -> !vehicleRepository.existsById(v.getId()))
                    .collect(Collectors.toList());
            // if there are new vehicles available, store them
            if (!newVehiclesAvailable.isEmpty()) {
                log.info("There are {} vehicles that are available now", newVehiclesAvailable.size());
                vehicleRepository.saveAll(newVehiclesAvailable);
            }

            // and finally, we need to check vehicles that aren't available right now
            List<Vehicle> vehiclesUnavailable = vehicleRepository.findAll().stream()
                    .filter(v -> !vehicles.contains(v))
                    .collect(Collectors.toList());
            // if there are new vehicles available, store them
            if (!vehiclesUnavailable.isEmpty()) {
                log.info("There are {} vehicles that are not available now", vehiclesUnavailable.size());
                vehicleRepository.deleteAll(vehiclesUnavailable);
            }

            // trace a final log to show the number of vehicles registered at this time
            log.info("Now there are {} vehicles available", vehicleRepository.count());
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
                    log.warn("Cannot obtain vehicles info server responds with [{}], falling back to a default values", ex.getMessage());
                    return Mono.just(generateRandomList());
                })
                .block();
    }

    /**
     * Generates a random list of vehicles which are included randomly to simulate their availability
     *
     * @return a list of vehicles
     */
    private List<Vehicle> generateRandomList() {
        // create a range between 0 and 9 (both inclusive)
        // and map them to a list of vehicles which are included randomly to simulate their availability
        return IntStream.rangeClosed(0, 9)
                .boxed()
                .filter(i -> include())
                .map(i -> new Vehicle("PT - LIS - A0014" + i, "13VJ6" + i, -9.139704, 38.73481, "13VJ6" + i, 50L + i, 68, 2, "Askoll", "vehicle_gen_ecooltra", List.of("vehicle_gen_ecooltra"), true, "MOPED", 473L))
                .collect(Collectors.toList());
    }

    /**
     * Returns true or false randomly (uses a random function to obtain a number between 1 and 10, and if the number
     * is even returns true, otherwise returns false)
     *
     * @return true or false randomly
     */
    private boolean include() {
        // create the max/min values
        int max = 10, min = 1;
        // generate the random num
        int num = new Random().nextInt(max - min + 1) + min;
        // check if the number is even to return true or false
        return num % 2 == 0;
    }
}
