package org.demo.util;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author zacconding
 * @Date 2018-11-01
 * @GitHub : https://github.com/zacscoding
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String findValue(String json, String key) throws IOException {
        return findValue(OBJECT_MAPPER.readTree(json), key);
    }

    public static String findValue(JsonNode node, String key) {
        if (key == null || key.length() == 0) {
            return null;
        }

        int idx = key.indexOf('.');
        if (idx < 0) {
            return node.has(key) ? node.get(key).asText() : null;
        } else {
            String currentKey = key.substring(0, idx);
            String remain = key.substring(idx + 1);

            return node.has(currentKey) ? (findValue(node.get(currentKey), remain)) : null;
        }
    }
}
