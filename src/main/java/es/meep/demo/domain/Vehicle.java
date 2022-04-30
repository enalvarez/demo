package es.meep.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Model for vehicle information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Vehicle {
    /**
     * Identifier of the vehicle
     */
    @Id
    @EqualsAndHashCode.Include
    private String id;
    /**
     * Name of the vehicle (same as license plate in this case)
     */
    private String name;
    /**
     * Coordinate X (longitude) where the vehicle is located
     */
    private Double x;
    /**
     * Coordinate Y (latitude) where the vehicle is located
     */
    private Double y;
    /**
     * The license plate of the vehicle
     */
    private String licencePlate;
    /**
     * The approximate range between latitude and longitude where the vehicle is located (?)
     */
    private Long range;
    /**
     * The battery level of the vehicle
     */
    private Integer batteryLevel;
    /**
     * The number of helmets that the vehicle has
     */
    private Integer helmets;
    /**
     * The model of the vehicle
     */
    private String model;
    /**
     * The identifier of the resource image
     */
    private String resourceImageId;
    /**
     * The list of images' URLs
     */
    @Convert(converter = StringListConverter.class)
    private List<String> resourcesImagesUrls;
    /**
     * A flag to indicate if it's a real time info
     */
    private Boolean realTimeData;
    /**
     * The resource type
     */
    private String resourceType;
    /**
     * The company zone identifier
     */
    private Long companyZoneId;
}
