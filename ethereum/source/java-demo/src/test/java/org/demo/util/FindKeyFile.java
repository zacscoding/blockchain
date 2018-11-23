package org.demo.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zacconding
 * @Date 2018-10-19
 * @GitHub : https://github.com/zacscoding
 */
public class FindKeyFile {

    public static File findKeyFile(String dirName, Predicate<String> contentPredicate) {
        File dir = new File(dirName);

        for (File file : dir.listFiles()) {
            try {
                if (!file.getName().startsWith("UTC")) {
                    continue;
                }
                String content = Files.readAllLines(file.toPath()).stream().collect(Collectors.joining(""));
                if (contentPredicate.test(content)) {
                    return file;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
