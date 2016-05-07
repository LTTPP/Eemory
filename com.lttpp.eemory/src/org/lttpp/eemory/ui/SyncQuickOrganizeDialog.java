package org.lttpp.eemory.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.lttpp.eemory.util.LogUtil;

public class SyncQuickOrganizeDialog {

    private final Shell shell;
    private int option = QuickOrganizeDialog.CANCEL;

    public SyncQuickOrganizeDialog(final Shell shell) {
        this.shell = shell;
    }

    public int show() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    option = QuickOrganizeDialog.show(shell);
                } catch (Exception e) {
                    LogUtil.logError(e);
                }
            }
        });
        return option;
    }

}
