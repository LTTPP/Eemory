package com.prairie.eevernote.enml;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.ArrayUtil;
import com.prairie.eevernote.util.ColorUtil;
import com.prairie.eevernote.util.DomUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.StringUtil;

public class Snippet implements Constants {
	private List<Node> nodes;

	private Document doc;

	public Snippet(StyledText styledText) throws ParserConfigurationException {
		doc = DomUtil.getBuilder().newDocument();
		toSnippet(styledText);
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> snippet) {
		this.nodes = snippet;
	}

	public void addNode(Node node) {
		if (ListUtil.nullList(nodes)) {
			nodes = ListUtil.list();
		}
		nodes.add(node);
	}

	public boolean isNullOrEmpty() {
		return ListUtil.nullOrEmptyList(nodes);
	}

	private void toSnippet(StyledText styledText) throws DOMException, ParserConfigurationException {
		Point selection = styledText.getSelection();
		String selectionText = styledText.getSelectionText();

		String face = StringUtil.EMPTY;
		int size = TEN;
		FontData[] fontDatas = styledText.getFont().getFontData();
		if (fontDatas != null && fontDatas.length > ZERO) {
			face = fontDatas[ZERO].getName();
			size = fontDatas[ZERO].getHeight();
		}
		String[] lines = StringUtils.splitByWholeSeparatorPreserveAllTokens(selectionText, StringUtil.CRLF);
		int count = ZERO;
		for (int i = ZERO; i < lines.length; i++) {
			int offset = selection.x + (count += (i <= ZERO ? ZERO : lines[i - ONE].length())) + (i * TWO);
			StyleRange[] ranges = styledText.getStyleRanges(offset, lines[i].length());
			StyleTextRange[] textRanges = parse(lines[i], ranges, offset);
			nodes.add(div(textRanges, face, String.valueOf(size)));
		}
	}

	private StyleTextRange[] parse(String text, StyleRange[] styleRanges, int offset) {
		if (ArrayUtil.nullOrEmptyArray(styleRanges)) {
			StyleTextRange textRange = new StyleTextRange(text);
			return new StyleTextRange[] { textRange };
		}

		List<StyleTextRange> textRanges = ListUtil.list();
		int count = 0;
		for (int i = 0; i < styleRanges.length; i++) {
			int start = styleRanges[i].start - offset;

			String part = text.substring(count, start);
			if (!StringUtil.nullOrEmptyString(part)) {
				StyleTextRange textRange = new StyleTextRange(part);
				textRanges.add(textRange);
				count += part.length();
			}

			part = text.substring(start, start + styleRanges[i].length);
			StyleTextRange textRange = new StyleTextRange(part, styleRanges[i]);
			textRanges.add(textRange);
			count += part.length();
		}
		String part = text.substring(count);
		if (!StringUtil.nullOrEmptyString(part)) {
			StyleTextRange textRange = new StyleTextRange(part);
			textRanges.add(textRange);
		}

		return textRanges.toArray(new StyleTextRange[textRanges.size()]);
	}

	private Node div(StyleTextRange[] textRanges, String face, String size) throws DOMException, ParserConfigurationException {
		Element div = doc.createElement("div");
		for (StyleTextRange range : textRanges) {
			Color color = range.getStyleRange().foreground != null ? range.getStyleRange().foreground : ColorUtil.SWT_DEFAULT_COLOR;
			String colorHexCode = ColorUtil.toHexCode(color.getRed(), color.getGreen(), color.getBlue());

			String escapedXml = StringUtil.escapeEnml(range.getText());

			div.appendChild(font(escapedXml, face, colorHexCode, size, range.getStyleRange().fontStyle));
		}
		return div;
	}

	private Node font(String text, String face, String color, String size, int fontStyle) throws DOMException, ParserConfigurationException {
		Element font = doc.createElement("font");
		font.appendChild(span(text, size, fontStyle));
		font.setAttribute("color", color);
		font.setAttribute("color", color);
		font.setAttribute("size", String.valueOf(TWO));
		return font;
	}

	private Node span(String text, String size, int fontStyle) throws DOMException, ParserConfigurationException {
		Element span = doc.createElement("span");
		if (StringUtil.nullOrEmptyString(text)) {
			span.appendChild(doc.createElement("br"));
		} else {
			FontStyle style = FontStyle.NORMAL;
			try {
				style = FontStyle.forNumber(fontStyle);
			} catch (IllegalArgumentException e) {
				style = FontStyle.NORMAL;
			}
			if (style == FontStyle.BOLD) {
				span.appendChild(b(text));
			} else if (style == FontStyle.ITALIC) {
				span.appendChild(i(text));
			} else if (style == FontStyle.NORMAL) {
				span.appendChild(text(text));
			}
		}
		span.setAttribute("style", "font-size:" + size + "pt");
		return span;
	}

	private Node b(String text) throws DOMException, ParserConfigurationException {
		Node b = doc.createElement("b");
		b.setTextContent(text);
		return b;
	}

	private Node i(String text) throws DOMException, ParserConfigurationException {
		Node b = doc.createElement("i");
		b.setTextContent(text);
		return b;
	}

	private Node text(String text) throws DOMException, ParserConfigurationException {
		return doc.createTextNode(text);
	}
}