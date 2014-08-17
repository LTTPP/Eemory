package com.prairie.eevernote.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.enml.B;
import com.prairie.eevernote.enml.Br;
import com.prairie.eevernote.enml.Div;
import com.prairie.eevernote.enml.Font;
import com.prairie.eevernote.enml.FontStyle;
import com.prairie.eevernote.enml.I;
import com.prairie.eevernote.enml.Span;
import com.prairie.eevernote.enml.TextRange;

public class EnmlUtil implements Constants {

    private static TransformerHandler transformerHandler;

    public static void validate(String enml) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
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
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        reader.parse(new InputSource(new ByteArrayInputStream(enml.getBytes(CharEncoding.UTF_8))));
    }

    public static TransformerHandler getTransformerHandler() throws TransformerConfigurationException, FileNotFoundException {
        if (transformerHandler == null) {
            SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            transformerHandler = factory.newTransformerHandler();

            OutputStream output = new ByteArrayOutputStream();
            transformerHandler.setResult(new StreamResult(output));

            Transformer transformer = transformerHandler.getTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        }
        return transformerHandler;
    }

    // 30M - 220ms
    public static String toSnippet(StyledText styledText) {
        System.out.println("parsing...");
        long start = System.currentTimeMillis();
        Point selection = styledText.getSelection();
        String selectionText = styledText.getSelectionText();

        String face = StringUtil.EMPTY;
        int size = TEN;
        FontData[] fontDatas = styledText.getFont().getFontData();
        if (fontDatas != null && fontDatas.length > ZERO) {
            face = fontDatas[ZERO].getName();
            size = fontDatas[ZERO].getHeight();
        }
        String snippet = StringUtil.EMPTY;
        String[] lines = StringUtils.splitByWholeSeparatorPreserveAllTokens(selectionText, StringUtil.CRLF);
        int count = ZERO;
        for (int i = ZERO; i < lines.length; i++) {
            int offset = selection.x + (count += (i <= ZERO ? ZERO : lines[i - ONE].length())) + (i * TWO);
            StyleRange[] ranges = styledText.getStyleRanges(offset, lines[i].length());
            TextRange[] textRanges = parse(lines[i], ranges, offset);
            snippet += div(textRanges, face, String.valueOf(size));
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
        return snippet;
    }

    private static TextRange[] parse(String text, StyleRange[] styleRanges, int offset) {
        if (ArrayUtil.nullOrEmptyArray(styleRanges)) {
            TextRange textRange = new TextRange(text);
            return new TextRange[] { textRange };
        }

        List<TextRange> textRanges = ListUtil.list();
        int count = 0;
        for (int i = 0; i < styleRanges.length; i++) {
            int start = styleRanges[i].start - offset;

            String part = text.substring(count, start);
            if (!StringUtil.nullOrEmptyString(part)) {
                TextRange textRange = new TextRange(part);
                textRanges.add(textRange);
                count += part.length();
            }

            part = text.substring(start, start + styleRanges[i].length);
            TextRange textRange = new TextRange(part, styleRanges[i]);
            textRanges.add(textRange);
            count += part.length();
        }
        String part = text.substring(count);
        if (!StringUtil.nullOrEmptyString(part)) {
            TextRange textRange = new TextRange(part);
            textRanges.add(textRange);
        }

        return textRanges.toArray(new TextRange[textRanges.size()]);
    }

    private static Div div(TextRange[] textRanges, String face, String size) {
        Div div = new Div();
        for (TextRange range : textRanges) {
            Color color = range.getStyleRange().foreground != null ? range.getStyleRange().foreground : ColorUtil.SWT_DEFAULT_COLOR;
            String colorHexCode = ColorUtil.toHexCode(color.getRed(), color.getGreen(), color.getBlue());

            String escapedXml = StringUtil.escapeEnml(range.getText());

            div.addFont(font(escapedXml, face, colorHexCode, size, range.getStyleRange().fontStyle));
        }
        return div;
    }

    private static Font font(String text, String face, String color, String size, int fontStyle) {
        Font font = new Font();
        font.addSpan(span(text, size, fontStyle));
        font.setColor(color);
        font.setFace(face);
        font.setSize(String.valueOf(TWO));
        return font;
    }

    private static Span span(String text, String size, int fontStyle) {
        Span span = new Span();
        if (StringUtil.nullOrEmptyString(text)) {
            span.addBr(new Br());
        } else {
            FontStyle style = FontStyle.NORMAL;
            try {
                style = FontStyle.forNumber(fontStyle);
            } catch (IllegalArgumentException e) {
                style = FontStyle.NORMAL;
            }
            if (style == FontStyle.BOLD) {
                span.addBold(new B(text));
            } else if (style == FontStyle.ITALIC) {
                span.addItalic(new I(text));
            } else if (style == FontStyle.NORMAL) {
                span.addText(text);
            }
        }
        span.setFontSize(size);
        return span;
    }

}
