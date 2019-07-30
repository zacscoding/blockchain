package demo.common;

import java.awt.image.BufferedImageOp;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestReflectionUtils {

    /**
     * Display getter methods (starts with getXXX)
     */
    public static String displayGetterMethods(Object instance) {
        Class<?> clazz = instance.getClass();

        StringBuilder builder = new StringBuilder("// ===================================");
        builder.append("## Display getter methods [")
            .append(clazz.getSimpleName())
            .append("]\n");

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.getName().startsWith("get")) {
                continue;
            }

            if (method.getParameterCount() != 0) {
                continue;
            }

            builder.append(method.getName())
                .append("() : ");
            try {
                builder.append(
                    method.invoke(instance, null)
                );
            } catch (Exception e) {
                builder.append(
                    e.getMessage()
                );
            }

            builder.append("\n");
        }

        return builder.toString();
    }
}
