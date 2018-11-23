package org.demo.parity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-06-07
 * @GitHub : https://github.com/zacscoding
 */
public class ParityJsonMethodToFieldTest {

    @Test
    public void readAndWrite() throws Exception {
        ClassPathResource resource = new ClassPathResource("parity-json.txt");

        File parityJsonMethods = new ClassPathResource("parity-json.txt").getFile();
        BufferedReader reader = new BufferedReader(new FileReader(parityJsonMethods));
        PrintStream ps = new PrintStream(new FileOutputStream("field.txt"));

        String readLine = null;
        while ((readLine = reader.readLine()) != null) {
            if (!StringUtils.hasText(readLine)) {
                ps.println();
            }

            String newField = "public static String " + readLine.replace("\n", "").trim() + ";";
            ps.println(newField);
        }
    }

}
