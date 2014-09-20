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
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Shell;
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
            if (iFile != null) {// TODO iFile == null: how to handle this case in XML file
                File file = iFile.getLocation().makeAbsolute().toFile();
                files.add(file);
            }
        }

        return files;
    }

    public static List<List<StyleText>> getSelectedStyleText(final StyledText styledText) throws DOMException, ParserConfigurationException {
        Point selection = styledText.getSelection();
        String selectionText = styledText.getSelectionText();
        if (StringUtils.isEmpty(selectionText)) {
            return ListUtil.list();
        }

        String face = StringUtils.EMPTY;
        int size = TEN;//TODO why ten
        FontData[] fontDatas = styledText.getFont().getFontData(); // TODO why array here
        if (fontDatas != null && fontDatas.length > ZERO) {
            face = fontDatas[ZERO].getName();
            size = fontDatas[ZERO].getHeight();
        }

        List<List<StyleText>> list = ListUtil.list();
        int start = ZERO;
        while (start >= ZERO) {
            int end = StringUtil.indexOfAny(selectionText, ArrayUtils.toArray(StringUtil.CRLF, StringUtils.CR, StringUtils.LF), start);

            String line = selectionText.substring(start, end < 0 ? selectionText.length() : end);

            int offset = selection.x + start;
            StyleRange[] ranges = styledText.getStyleRanges(offset, line.length());
            List<StyleText> textRanges = parseLine(line, ranges, offset, face, String.valueOf(size));
            list.add(textRanges);

            start = end < ZERO ? end : end + (selectionText.startsWith(StringUtil.CRLF, end) ? StringUtil.CRLF.length() : selectionText.startsWith(StringUtils.CR, end) ? StringUtils.CR.length() : StringUtils.LF.length());
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

    public static int openWarningWithMultipleButtons(final Shell shell, final String title, final String message, final String[] buttons) {
        MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.WARNING, buttons, ZERO);
        return dialog.open();
    }

    public static int openErrorWithMultipleButtons(final Shell shell, final String title, final String message, final String[] buttons) {
        MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.WARNING, buttons, ZERO);
        return dialog.open();
    }

}
