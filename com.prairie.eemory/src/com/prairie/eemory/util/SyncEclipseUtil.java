package com.prairie.eemory.util;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SyncEclipseUtil {

    private int opt = 0;

    public int openCustomImageTypeWithCustomButtonsSyncly(final Shell shell, final String title, final String message, final Image dialogImage, final String[] buttons) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                opt = EclipseUtil.openCustomImageTypeWithCustomButtons(shell, title, message, dialogImage, buttons);
            }
        });
        return opt;
    }

}
