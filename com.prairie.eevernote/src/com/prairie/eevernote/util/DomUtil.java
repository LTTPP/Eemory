package com.prairie.eevernote.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eevernote.dom.DocumentBuilder;
import com.prairie.eevernote.dom.DocumentBuilderFactory;
import com.prairie.eevernote.dom.FactoryConfigurationError;
import com.prairie.eevernote.dom.Node;

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
