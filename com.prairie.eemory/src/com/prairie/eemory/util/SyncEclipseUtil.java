package com.prairie.eemory.util;

import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SyncEclipseUtil {

    private String opt;

    public String openCustomImageTypeWithCustomButtonsSyncly(final Shell shell, final String title, final String message, final Image dialogImage, final LinkedHashMap<String, String> buttons) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                opt = EclipseUtil.openCustomImageTypeWithCustomButtons(shell, title, message, dialogImage, buttons);
            }
        });
        return opt;
    }

}
