package org.lttpp.eemory.enml;

import static org.lttpp.eemory.Constants.ENML_ATTR_COLOR;
import static org.lttpp.eemory.Constants.ENML_ATTR_FACE;
import static org.lttpp.eemory.Constants.ENML_ATTR_FONT;
import static org.lttpp.eemory.Constants.ENML_ATTR_HASH;
import static org.lttpp.eemory.Constants.ENML_ATTR_STYLE;
import static org.lttpp.eemory.Constants.ENML_ATTR_TYPE;
import static org.lttpp.eemory.Constants.ENML_DOCTYPE_DECLARATION_SYSTEM_ID;
import static org.lttpp.eemory.Constants.ENML_DTD;
import static org.lttpp.eemory.Constants.ENML_DTD_LOCATION;
import static org.lttpp.eemory.Constants.ENML_TAG_BOLD;
import static org.lttpp.eemory.Constants.ENML_TAG_BR;
import static org.lttpp.eemory.Constants.ENML_TAG_DIV;
import static org.lttpp.eemory.Constants.ENML_TAG_EN_MEDIA;
import static org.lttpp.eemory.Constants.ENML_TAG_EN_NOTE;
import static org.lttpp.eemory.Constants.ENML_TAG_EN_NOTE_SELF_CLOSING_REGEX;
import static org.lttpp.eemory.Constants.ENML_TAG_EN_NOTE_SELF_CLOSING_REPLACEMENT;
import static org.lttpp.eemory.Constants.ENML_TAG_EN_NOTE_START_REGEX;
import static org.lttpp.eemory.Constants.ENML_TAG_EN_NOTE_START_REPLACEMENT_P1;
import static org.lttpp.eemory.Constants.ENML_TAG_ITALIC;
import static org.lttpp.eemory.Constants.ENML_TAG_SPAN;
import static org.lttpp.eemory.Constants.ENML_VALUE_FONT_SIZE;
import static org.lttpp.eemory.Constants.ENML_VALUE_PT;
import static org.lttpp.eemory.Constants.XHTML_1_0_LATIN_1_ENT;
import static org.lttpp.eemory.Constants.XHTML_1_0_LATIN_1_ENT_LOCATION;
import static org.lttpp.eemory.Constants.XHTML_1_0_SPECIAL_ENT;
import static org.lttpp.eemory.Constants.XHTML_1_0_SPECIAL_ENT_LOCATION;
import static org.lttpp.eemory.Constants.XHTML_1_0_SYMBOL_ENT;
import static org.lttpp.eemory.Constants.XHTML_1_0_SYMBOL_ENT_LOCATION;
import static org.lttpp.eemory.Constants.XML_VERSION_1_0;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.Constants;
import org.lttpp.eemory.dom.DOMException;
import org.lttpp.eemory.dom.Document;
import org.lttpp.eemory.dom.Element;
import org.lttpp.eemory.dom.Node;
import org.lttpp.eemory.util.ConstantsUtil;
import org.lttpp.eemory.util.DomUtil;
import org.lttpp.eemory.util.ListUtil;
import org.lttpp.eemory.util.LogUtil;
import org.lttpp.eemory.util.StringEscapeUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class ENML {

    // used to create new note
    private final Document document;
    private Node root;
    // used for updating existing note
    private String existingEnml = StringUtils.EMPTY;
    private final List<Node> newAddedNodes;

    private int tabWidth;

    public ENML() throws DOMException, ParserConfigurationException {
        document = DomUtil.getBuilder().newDocument();
        document.setXmlStandalone(true);
        document.setXMLEncoding(CharEncoding.UTF_8);
        document.setXmlVersion(XML_VERSION_1_0);

        document.appendChild(document.createDocumentType(ENML_TAG_EN_NOTE, null, ENML_DOCTYPE_DECLARATION_SYSTEM_ID));

        root = document.createElement(ENML_TAG_EN_NOTE);
        document.appendChild(root);

        newAddedNodes = ListUtil.list();

        tabWidth = Constants.TAB_WIDTH;
    }

    public ENML(final String enml) {
        document = DomUtil.getBuilder().newDocument();
        existingEnml = enml;
        newAddedNodes = ListUtil.list();

        tabWidth = Constants.TAB_WIDTH;
    }

    public int getTabWidth() {
        return tabWidth;
    }

    public void setTabWidth(final int tabWidth) {
        this.tabWidth = tabWidth;
    }

    public void addResource(final String hashHex, final String mimeType) throws DOMException, ParserConfigurationException {
        if (StringUtils.isNotBlank(hashHex)) {
            Element div = div();
            div.appendChild(media(hashHex, mimeType));
            newAddedNodes.add(div);
        }
    }

    public void addComment(final String comments) throws DOMException, ParserConfigurationException {
        if (StringUtils.isNotBlank(comments)) {
            Element div = div();
            div.setTextContent(StringEscapeUtil.escapeEnml(comments) + ConstantsUtil.COLON);
            newAddedNodes.add(div);
        }
    }

    public void addContent(final List<List<StyleText>> content) throws DOMException, ParserConfigurationException {
        List<Node> list = parseStyleText(content);
        newAddedNodes.addAll(list);
    }

    /**
     * Get the string representation of ENML.
     *
     * @return the string representation of ENML
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public String get() throws ParserConfigurationException, SAXException, IOException {
        if (isCreateNew()) {
            for (Node n : newAddedNodes) {
                root.appendChild(n);
            }
            String newEnml = DomUtil.toString(document);
            validateENML(newEnml);
            LogUtil.debug(newEnml);
            return newEnml;
        } else {
            if (!ListUtil.isNullOrEmptyList(newAddedNodes)) {
                Element div = div();
                div.appendChild(br());
                newAddedNodes.add(div);
            }

            // open self-closing en-note tag <en-note ... /> to <en-note ... ></en-note> for following processing
            existingEnml = StringUtils.replacePattern(existingEnml, ENML_TAG_EN_NOTE_SELF_CLOSING_REGEX, ENML_TAG_EN_NOTE_SELF_CLOSING_REPLACEMENT);
            // insert new nodes
            existingEnml = StringUtils.replacePattern(existingEnml, ENML_TAG_EN_NOTE_START_REGEX, ENML_TAG_EN_NOTE_START_REPLACEMENT_P1 + Matcher.quoteReplacement(DomUtil.toString(newAddedNodes)));

            validateENML(existingEnml);
            LogUtil.debug(existingEnml);
            return existingEnml;
        }
    }

    private boolean isCreateNew() {
        return document != null && root != null && StringUtils.isBlank(existingEnml);
    }

    private Element div() throws DOMException, ParserConfigurationException {
        return document.createElement(ENML_TAG_DIV);
    }

    private Element br() {
        return document.createElement(ENML_TAG_BR);
    }

    private Element media(final String hashHex, final String mimeType) throws DOMException, ParserConfigurationException {
        Element media = document.createElement(ENML_TAG_EN_MEDIA);
        //media.setAttribute(ENML_ATTR_ALIGN, Alignment.LEFT.toString()); // should not have this which break display in browser
        media.setAttribute(ENML_ATTR_TYPE, mimeType);
        media.setAttribute(ENML_ATTR_HASH, hashHex);
        return media;
    }

    private List<Node> parseStyleText(final List<List<StyleText>> styleTextBlocks) throws DOMException, ParserConfigurationException {
        List<Node> list = ListUtil.list();
        for (List<StyleText> lineBlocks : styleTextBlocks) {
            list.add(div(lineBlocks));
        }
        return list;
    }

    private Node div(final List<StyleText> styleTextBlocks) throws DOMException, ParserConfigurationException {
        Element div = document.createElement(ENML_TAG_DIV);
        for (StyleText styletext : styleTextBlocks) {
            String escapedXml = StringEscapeUtil.escapeEnml(styletext.getText(), tabWidth);
            div.appendChild(font(escapedXml, styletext.getFace(), styletext.getColorHexCode(), styletext.getSize(), styletext.getFontStyle()));
        }
        return div;
    }

    private Node font(final String text, final String face, final String color, final String size, final FontStyle fontStyle) throws DOMException, ParserConfigurationException {
        Element font = document.createElement(ENML_ATTR_FONT);
        font.appendChild(span(text, size, fontStyle));
        font.setAttribute(ENML_ATTR_FACE, face);
        font.setAttribute(ENML_ATTR_COLOR, color);
        //font.setAttribute(ENML_ATTR_SIZE, String.valueOf(2));// does not work if style="font-size:xxpt" of span set
        return font;
    }

    private Node span(final String text, final String size, final FontStyle fontStyle) throws DOMException, ParserConfigurationException {
        Element span = document.createElement(ENML_TAG_SPAN);
        if (StringUtils.isEmpty(text)) {
            span.appendChild(br());
        } else {
            if (fontStyle == FontStyle.BOLD) {
                span.appendChild(b(text));
            } else if (fontStyle == FontStyle.ITALIC) {
                span.appendChild(i(text));
            } else if (fontStyle == FontStyle.NORMAL) {
                span.appendChild(text(text));
            } else if (fontStyle == FontStyle.BOLD_ITALIC) {
                span.appendChild(bi(text));
            }
        }
        span.setAttribute(ENML_ATTR_STYLE, ENML_VALUE_FONT_SIZE + size + ENML_VALUE_PT);
        return span;
    }

    private Node b(final String text) throws DOMException, ParserConfigurationException {
        Node b = document.createElement(ENML_TAG_BOLD);
        b.setTextContent(text);
        return b;
    }

    private Node i(final String text) throws DOMException, ParserConfigurationException {
        Node i = document.createElement(ENML_TAG_ITALIC);
        i.setTextContent(text);
        return i;
    }

    private Node bi(final String text) throws DOMException, ParserConfigurationException {
        Node i = document.createElement(ENML_TAG_ITALIC);
        i.setTextContent(text);

        Node b = document.createElement(ENML_TAG_BOLD);
        b.appendChild(i);

        return b;
    }

    private Node text(final String text) throws DOMException, ParserConfigurationException {
        return document.createTextNode(text);
    }

    /**
     * Validate ENML string.
     *
     * @param enml
     *            ENML string to be validated
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static void validateENML(final String enml) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
                if (systemId.endsWith(ENML_DTD)) {
                    return new InputSource(getClass().getResourceAsStream(ENML_DTD_LOCATION));
                } else if (systemId.endsWith(XHTML_1_0_LATIN_1_ENT)) {
                    return new InputSource(getClass().getResourceAsStream(XHTML_1_0_LATIN_1_ENT_LOCATION));
                } else if (systemId.endsWith(XHTML_1_0_SYMBOL_ENT)) {
                    return new InputSource(getClass().getResourceAsStream(XHTML_1_0_SYMBOL_ENT_LOCATION));
                } else if (systemId.endsWith(XHTML_1_0_SPECIAL_ENT)) {
                    return new InputSource(getClass().getResourceAsStream(XHTML_1_0_SPECIAL_ENT_LOCATION));
                } else {
                    return null;
                }
            }
        });
        reader.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(final SAXParseException exception) throws SAXException {
                LogUtil.logWarning(enml);
                throw exception;
            }

            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
                LogUtil.logError(enml);
                throw exception;
            }

            @Override
            public void error(final SAXParseException exception) throws SAXException {
                LogUtil.logError(enml);
                throw exception;
            }
        });
        reader.parse(new InputSource(new ByteArrayInputStream(enml.getBytes(CharEncoding.UTF_8))));
    }

}
