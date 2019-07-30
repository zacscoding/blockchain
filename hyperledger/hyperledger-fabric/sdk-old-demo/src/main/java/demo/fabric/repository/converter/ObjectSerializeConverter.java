package demo.fabric.repository.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.StringUtils;

/**
 *
 */
@Slf4j
public class ObjectSerializeConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (attribute == null) {
                return null;
            }

            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            bos.close();
            return Hex.toHexString(bos.toByteArray());
        } catch (IOException e) {
            logger.warn("IOException occur while serializing object", e);
            return null;
        }
    }

    @Override
    public Object convertToEntityAttribute(String serializedHex) {
        if (!StringUtils.hasText(serializedHex)) {
            return null;
        }

        byte[] serialized = Hex.decode(serializedHex);
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            logger.warn("Exception occur while deserializing object", e);
            return null;
        }
    }
}
