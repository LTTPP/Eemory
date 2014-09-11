package com.prairie.eevernote.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMUserException;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.util.LogUtil;

public class EDAMUserExceptionHandler {

    public IStatus handleRuntime(final EDAMUserException e) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, "EEvernote is no longer authorized to access your Evernote account. (Click/Tap) below to re-authorize EEvernote");
        }
        return LogUtil.error(e);
    }

    public void handleDesingTime(final Shell shell, final EDAMUserException e) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    MessageDialog.openError(shell, "Auth Expired", "EEvernote is no longer authorized to access your Evernote account. (Click/Tap) below to re-authorize EEvernote");
                }
            });
        }
        LogUtil.logCancel(e);
    }

}
