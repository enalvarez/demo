package es.meep.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Contains all properties under prefix "meep"
 */
@Component
@ConfigurationProperties(prefix = "meep")
@Data
public class MeepDevProperties {
    /**
     * The base url of the Meep's server
     */
    private String baseUrl;
    /**
     * The endpoint to get data from
     */
    private String endpoint;
    /**
     * THe lower left latitude & longitude
     */
    private String lowerLeftLatLon;
    /**
     * THe upper right latitude & longitude
     */
    private String upperRightLatLon;
    /**
     * The list of zone identifiers
     */
    private String[] companyZoneIds;

    /**
     * Returns a formatted url with all the data contained in this class
     *
     * @return the full url
     */
    public String getFullUrl() {
        return baseUrl + endpoint + "?" +
                "lowerLeftLatLon=" + lowerLeftLatLon + "&" +
                "upperRightLatLon=" + upperRightLatLon + "&" +
                "companyZoneIds=" + String.join(",", companyZoneIds);
    }
}
