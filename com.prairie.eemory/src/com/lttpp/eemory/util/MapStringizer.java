package com.lttpp.eemory.util;

/**
 * An interface used to let implemented class have know how to convert objects
 * to string-map which means both key and value are string type.
 */
public interface MapStringizer {

    public String key(Object o);

    public String value(Object o);

}
