package com.prairie.eevernote.util;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.DOMException;

import com.prairie.eevernote.enml.FontStyle;
import com.prairie.eevernote.enml.StyleText;
import com.prairie.eevernote.ui.ConfigContentProposalProvider;
import com.prairie.eevernote.ui.ConfigTextContentAdapter;

public class EclipseUtil implements ConstantsUtil {

    public static List<File> getSelectedFiles(final ExecutionEvent event) {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        final List<File> files = ListUtil.list();

        if (selection instanceof IStructuredSelection) {
            Iterator<?> iterator = ((StructuredSelection) selection).iterator();
            while (iterator.hasNext()) {
                IFile iFile;
                Object object = iterator.next();
                if (object instanceof IFile) {
                    iFile = (IFile) object;
                } else if (object instanceof ICompilationUnit) {
                    ICompilationUnit compilationUnit = (ICompilationUnit) object;
                    IResource resource = compilationUnit.getResource();
                    if (resource instanceof IFile) {
                        iFile = (IFile) resource;
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
                File file = iFile.getLocation().makeAbsolute().toFile();
                files.add(file);
            }
        } else if (selection instanceof ITextSelection) {
            IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
            IFile iFile = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
            if (iFile != null) {// TODO iFile == null: how to handle this
                // case in XML file
                File file = iFile.getLocation().makeAbsolute().toFile();
                files.add(file);
            }
        }

        return files;
    }

    public static List<List<StyleText>> getSelectedStyleText(final StyledText styledText) throws DOMException, ParserConfigurationException {
        Point selection = styledText.getSelection();
        String selectionText = styledText.getSelectionText();

        String face = StringUtils.EMPTY;
        int size = TEN;
        FontData[] fontDatas = styledText.getFont().getFontData();
        if (fontDatas != null && fontDatas.length > ZERO) {
            face = fontDatas[ZERO].getName();
            size = fontDatas[ZERO].getHeight();
        }

        String[] lines = StringUtil.splitByMultipleSeparatorsPreserveAllTokens(selectionText, new String[] { StringUtil.CRLF, StringUtils.CR, StringUtils.LF });
        int count = ZERO;
        List<List<StyleText>> list = ListUtil.list();
        for (int i = ZERO; i < lines.length; i++) {
            int offset = selection.x + (count += i <= ZERO ? ZERO : lines[i - ONE].length()) + i * TWO;
            StyleRange[] ranges = styledText.getStyleRanges(offset, lines[i].length());
            List<StyleText> textRanges = parseLine(lines[i], ranges, offset, face, String.valueOf(size));
            list.add(textRanges);
        }
        return list;
    }

    // [PlainText][StyledText][PlainText]
    private static List<StyleText> parseLine(final String text, final StyleRange[] styleRanges, final int offset, final String face, final String size) {
        List<StyleText> textRanges = ListUtil.list();

        if (ArrayUtils.isEmpty(styleRanges)) {
            StyleText textRange = new StyleText(text);
            textRanges.add(textRange);
            return textRanges;
        }

        int count = ZERO;
        for (StyleRange styleRange : styleRanges) {
            int start = styleRange.start - offset;

            // [PlainText] - Part1
            String part = text.substring(count, start);
            if (!StringUtils.isEmpty(part)) {
                StyleText textRange = new StyleText(part);
                textRanges.add(textRange);
                count += part.length();
            }

            // // [StyledText]
            part = text.substring(start, start + styleRange.length);
            Color foreground = styleRange.foreground != null ? styleRange.foreground : ColorUtil.SWT_COLOR_DEFAULT;
            StyleText textRange = new StyleText(part, face, ColorUtil.toHexCode(foreground.getRed(), foreground.getGreen(), foreground.getBlue()), size, FontStyle.forNumber(styleRange.fontStyle));
            textRanges.add(textRange);
            count += part.length();
        }
        // [PlainText] - Part2
        String part = text.substring(count);
        if (!StringUtils.isEmpty(part)) {
            StyleText textRange = new StyleText(part);
            textRanges.add(textRange);
        }

        return textRanges;
    }

    public static SimpleContentProposalProvider enableFilteringContentAssist(final Control control, final String[] proposals, final String byOperator) {
        Arrays.sort(proposals);
        ConfigContentProposalProvider contentProposalProvider = new ConfigContentProposalProvider(proposals);
        contentProposalProvider.setFiltering(true);
        contentProposalProvider.setByOperator(byOperator);

        ConfigTextContentAdapter textContentAdapter = new ConfigTextContentAdapter();
        textContentAdapter.setByOperator(byOperator);

        new ContentProposalAdapter(control, textContentAdapter, contentProposalProvider, null, null);

        return contentProposalProvider;
    }

    public static SimpleContentProposalProvider enableFilteringContentAssist(final Control control, final String[] proposals) {
        Arrays.sort(proposals);
        SimpleContentProposalProvider contentProposalProvider = new SimpleContentProposalProvider(proposals);
        contentProposalProvider.setFiltering(true);

        TextContentAdapter textContentAdapter = new TextContentAdapter();

        ContentProposalAdapter contentProposalAdapter = new ContentProposalAdapter(control, textContentAdapter, contentProposalProvider, null, null);
        contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

        return contentProposalProvider;
    }

}
