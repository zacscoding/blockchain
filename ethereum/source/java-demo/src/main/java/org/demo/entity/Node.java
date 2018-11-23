package org.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zacconding
 * @Date 2018-06-08
 * @GitHub : https://github.com/zacscoding
 */
@Data
@AllArgsConstructor
public class Node {
    private String name;
    private String host;
    private String port;
    private String url;

    public String getUrl() {
        return new StringBuilder("http://")
            .append(host)
            .append(":")
            .append(port)
            .toString();
    }
}
