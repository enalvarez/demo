package es.meep.demo.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Converter to transform a list of string into a single string and back again.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    /**
     * Define the char to split items
     */
    private static final String SPLIT_CHAR = ",";

    /**
     * Converts the value stored in the entity attribute (a list of strings) into the data representation
     * to be stored in the database (a single string).
     *
     * @param stringList the entity attribute value to be converted
     * @return the converted data to be stored in the database column
     */
    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
    }

    /**
     * Converts the data stored in the database column (a single string) into the
     * value to be stored in the entity attribute (a list of strings).
     *
     * @param string the data from the database column to be converted
     * @return the converted value to be stored in the entity attribute
     */
    @Override
    public List<String> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(SPLIT_CHAR)) : Collections.emptyList();
    }
}