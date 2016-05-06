package com.lttpp.eemory.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.lttpp.eemory.dom.DocumentBuilder;
import com.lttpp.eemory.dom.DocumentBuilderFactory;
import com.lttpp.eemory.dom.FactoryConfigurationError;
import com.lttpp.eemory.dom.Node;

public class DomUtil {

    public static String toString(final Node node) {
        return node != null ? node.toString() : StringUtils.EMPTY;
    }

    public static String toString(final List<Node> nodeList) {
        String string = StringUtils.EMPTY;
        for (Node n : nodeList) {
            string += toString(n);
        }
        return string;
    }

    public static DocumentBuilder getBuilder() throws FactoryConfigurationError {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder;
    }

}
