package com.prairie.eemory.util;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.prairie.eemory.Messages;
import com.prairie.eemory.enml.FontStyle;
import com.prairie.eemory.enml.StyleText;
import com.prairie.eemory.exception.NoDataFoundException;
import com.prairie.eemory.ui.ConfigContentProposalProvider;
import com.prairie.eemory.ui.ConfigTextContentAdapter;

public class EclipseUtil {

    public static List<File> getSelectedFiles(final ExecutionEvent event) throws NoDataFoundException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        final List<File> files = ListUtil.list();

        if (selection instanceof ITextSelection) {
            IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
            if (editorPart == null) {
                throw new NoDataFoundException(Messages.Plugin_Error_NoFile);
            }
            IFile iFile = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
            if (iFile != null) {
                File file = iFile.getLocation().makeAbsolute().toFile();
                files.add(file);
            } else {
                throw new NoDataFoundException(Messages.Plugin_Error_NoFile);
            }
        } else if (selection instanceof IStructuredSelection) {
            Iterator<?> iterator = ((StructuredSelection) selection).iterator();
            while (iterator.hasNext()) {
                IFile iFile = null;
                Object object = iterator.next();
                if (object instanceof IFile) {
                    iFile = (IFile) object;
                } else if (object instanceof IAdaptable) {
                    IAdaptable adapt = (IAdaptable) object;
                    Object resource = adapt.getAdapter(IResource.class);
                    if (resource instanceof IFile) {
                        iFile = (IFile) resource;
                    }
                }
                if (iFile != null) {
                    File file = iFile.getLocation().makeAbsolute().toFile();
                    files.add(file);
                } else {
                    throw new NoDataFoundException(Messages.Plugin_Error_NoFile);
                }
            }
        }

        return files;
    }

    public static List<List<StyleText>> getSelectedStyleText(final StyledText styledText) {
        Point selection = styledText.getSelection();
        String selectionText = styledText.getSelectionText();
        if (StringUtils.isEmpty(selectionText)) {
            return ListUtil.list();
        }

        int size = 10; // 10 as default value in eclipse, will be overwritten by custom
        String face = StringUtils.EMPTY;
        FontStyle foreStyle = FontStyle.NORMAL;
        FontData[] fontDatas = styledText.getFont().getFontData(); // On Windows, only one FontData will be returned per font. On X however, a Font object may be composed of multiple X fonts.
        if (ArrayUtils.isNotEmpty(fontDatas)) {
            size = fontDatas[0].getHeight();
            try {
                foreStyle = FontStyle.forNumber(fontDatas[0].getStyle());
            } catch (IllegalArgumentException e) {
                foreStyle = FontStyle.NORMAL;
            }
            List<String> fontFamily = ListUtil.list();
            for (FontData f : fontDatas) {
                if (StringUtils.isNotBlank(f.getName())) {
                    fontFamily.add(f.getName());
                }
            }
            face = StringUtils.join(fontFamily, ConstantsUtil.COMMA);
        }

        List<List<StyleText>> list = ListUtil.list();
        int start = 0;
        while (start >= 0) {
            int end = StringUtil.indexOfAny(selectionText, ArrayUtils.toArray(StringUtil.CRLF, StringUtils.CR, StringUtils.LF), start);

            String line = selectionText.substring(start, end < 0 ? selectionText.length() : end);

            int offset = selection.x + start;
            StyleRange[] ranges = styledText.getStyleRanges(offset, line.length());
            List<StyleText> textRanges = parseLine(line, ranges, offset, face, String.valueOf(size), ObjectUtils.defaultIfNull(styledText.getForeground(), ColorUtil.SWT_COLOR_DEFAULT), foreStyle);
            list.add(textRanges);

            start = end < 0 ? end : end + (selectionText.startsWith(StringUtil.CRLF, end) ? StringUtil.CRLF.length() : selectionText.startsWith(StringUtils.CR, end) ? StringUtils.CR.length() : StringUtils.LF.length());
        }

        return list;
    }

    // [PlainText][StyledText][PlainText]
    private static List<StyleText> parseLine(final String text, final StyleRange[] styleRanges, final int offset, final String face, final String size, final Color defaultForeColor, final FontStyle defaultForeStyle) {
        List<StyleText> textRanges = ListUtil.list();

        if (ArrayUtils.isEmpty(styleRanges)) {
            StyleText textRange = new StyleText(text, face, ColorUtil.toHexCode(defaultForeColor.getRed(), defaultForeColor.getGreen(), defaultForeColor.getBlue()), size, defaultForeStyle);
            textRanges.add(textRange);
            return textRanges;
        }

        int count = 0;
        for (StyleRange styleRange : styleRanges) {
            int start = styleRange.start - offset;

            // [PlainText] - Part1
            String part = text.substring(count, start);
            if (!StringUtils.isEmpty(part)) {
                StyleText textRange = new StyleText(part, face, ColorUtil.toHexCode(defaultForeColor.getRed(), defaultForeColor.getGreen(), defaultForeColor.getBlue()), size, defaultForeStyle);
                textRanges.add(textRange);
                count += part.length();
            }

            // // [StyledText]
            part = text.substring(start, start + styleRange.length);
            Color foreground = styleRange.foreground != null ? styleRange.foreground : defaultForeColor;
            FontStyle fontStyle;
            try {
                fontStyle = FontStyle.forNumber(styleRange.fontStyle);
            } catch (IllegalArgumentException e) {
                fontStyle = defaultForeStyle;
            }
            StyleText textRange = new StyleText(part, face, ColorUtil.toHexCode(foreground.getRed(), foreground.getGreen(), foreground.getBlue()), size, fontStyle);
            textRanges.add(textRange);
            count += part.length();
        }
        // [PlainText] - Part2
        String part = text.substring(count);
        if (!StringUtils.isEmpty(part)) {
            StyleText textRange = new StyleText(part, face, ColorUtil.toHexCode(defaultForeColor.getRed(), defaultForeColor.getGreen(), defaultForeColor.getBlue()), size, defaultForeStyle);
            textRanges.add(textRange);
        }

        return textRanges;
    }

    public static boolean isBundleInstalled(final String bundleId) {
        return Platform.getBundle(bundleId) != null;
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

    public static void openErrorSyncly(final Shell shell, final String title, final String message) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(shell, title, message);
            }
        });
    }

    private static int opt = 0;

    public static int openErrorWithCustomButtonsSyncly(final Shell shell, final String title, final String message, final String[] buttons) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                opt = openErrorWithCustomButtons(shell, title, message, buttons);
            }
        });
        return opt;
    }

    public static int openCustomImageTypeWithCustomButtons(final Shell shell, final String title, final String message, final Image dialogImage, final String[] buttons) {
        MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.NONE, buttons, 0) {
            @Override
            public Image getImage() {
                return dialogImage;
            }
        };
        return dialog.open();
    }

    public static int openInformationWithCustomButtons(final Shell shell, final String title, final String message, final String[] buttons) {
        MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.INFORMATION, buttons, 0);
        return dialog.open();
    }

    public static int openQuestionWithCustomButtons(final Shell shell, final String title, final String message, final String[] buttons) {
        MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.QUESTION_WITH_CANCEL, buttons, 0);
        return dialog.open();
    }

    public static int openWarningWithCustomButtons(final Shell shell, final String title, final String message, final String[] buttons) {
        MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.WARNING, buttons, 0);
        return dialog.open();
    }

    public static int openErrorWithCustomButtons(final Shell shell, final String title, final String message, final String[] buttons) {
        MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.ERROR, buttons, 0);
        return dialog.open();
    }

}
