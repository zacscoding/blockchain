package demo.fabric.repository.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.persistence.AttributeConverter;

/**
 *
 */
public class SetAttributeConverter implements AttributeConverter<Set, String> {

    @Override
    public String convertToDatabaseColumn(Set attribute) {
        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Set convertToEntityAttribute(String jsonValue) {
        try {
            return new ObjectMapper().readValue(jsonValue, Set.class);
        } catch (Exception e) {
            return null;
        }
    }
}
