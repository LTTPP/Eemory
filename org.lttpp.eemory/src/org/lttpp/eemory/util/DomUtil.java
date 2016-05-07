package org.lttpp.eemory.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.dom.DocumentBuilder;
import org.lttpp.eemory.dom.DocumentBuilderFactory;
import org.lttpp.eemory.dom.FactoryConfigurationError;
import org.lttpp.eemory.dom.Node;

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
