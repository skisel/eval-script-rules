package com.skisel;

import java.util.HashMap;

/**
 * User: sergeykisel
 * Date: 17.03.14
 * Time: 12:45
 */
public class Context {

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public Object read(String value) {
        return map.get(value);
    }

    public void write(String name, Object value) {
        map.put(name, value);
    }

    public Double readDouble(String value) {
        return (Double) map.get(value);
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "Context{" +
                "map=" + map +
                '}';
    }
}
