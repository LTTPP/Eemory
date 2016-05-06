package com.lttpp.eemory.util;

/**
 * An interface used to let implemented class have know how to convert objects
 * to string-list which means list element is string type.
 */
public interface ListStringizer {

    public String element(Object o);

}
