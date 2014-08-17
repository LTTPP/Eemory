package com.prairie.eevernote.enml;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.xml.sax.SAXException;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.ArrayUtil;
import com.prairie.eevernote.util.ColorUtil;
import com.prairie.eevernote.util.EnmlUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.StringUtil;

public class ENML implements Constants {

	private String existingEnml = StringUtil.EMPTY;
	private ENNote note;

	public ENML() {
		note = new ENNote();
	}

	public ENML(String enml) {
		this.existingEnml = enml;
	}

	private Div resource(String hashHex, String mimeType) {
		Media media = new Media();
		media.setAlign(Alignment.LEFT.name());
		media.setHash(hashHex);
		media.setType(mimeType);

		Div div = new Div();
		div.addMedia(media);
		return div;
	}

	private Div comments(String comments) {
		if (StringUtil.nullOrEmptyOrBlankString(comments)) {
			return null;
		}
		Div div = new Div();
		div.addText(comments + COLON);
		return div;
	}

	public void addResource(String hashHex, String mimeType) {
		if (!StringUtil.nullOrEmptyOrBlankString(hashHex)) {
			note.addDiv(resource(hashHex, mimeType));
		}
	}

	public void addComment(String comments) {
		if (!StringUtil.nullOrEmptyOrBlankString(comments)) {
			note.addDiv(comments(comments));
		}
	}

	public void addContent(Div div) {
		note.addDiv(div);
	}

	public void addSnippet(String snippet) {
		note.addSnippet(snippet);
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
				span.addBold(new Bold(text));
			} else if (style == FontStyle.ITALIC) {
				span.addItalic(new Italic(text));
			} else if (style == FontStyle.NORMAL) {
				span.addText(text);
			}
		}
		span.setFontSize(size);
		return span;
	}

	public String get() throws ParserConfigurationException, SAXException, IOException {
		if (!StringUtil.nullOrEmptyOrBlankString(existingEnml)) {
			String beginPart = existingEnml.substring(0, existingEnml.indexOf(NOTE_START) + NOTE_START.length());
			existingEnml = existingEnml.replace(beginPart, beginPart + note);
			EnmlUtil.validate(existingEnml);
			return existingEnml;
		} else {
			String newEnml = XML_DECLARATION + DOCTYPE_DECLARATION + note;
			EnmlUtil.validate(newEnml);
			return newEnml;
		}
	}
}
