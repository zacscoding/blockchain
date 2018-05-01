package org.web3jtest.util;

/**
 * @author zacconding
 * @Date 2018-05-02
 * @GitHub : https://github.com/zacscoding
 */
public class CompareUtil {

    public static boolean equals(Object o1, Object o2) {
        if(o1 == null) {
            return o2 == null;
        }

        return o1.equals(o2);
    }
}
