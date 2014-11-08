package com.prairie.eevernote.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMUserException;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.Messages;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.LogUtil;

public class EDAMUserExceptionHandler {

    public IStatus handleRuntime(final EDAMUserException e) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, Messages.Plugin_Runtime_AuthExpired_Message);
        }
        return LogUtil.error(e);
    }

    public void handleDesingTime(final Shell shell, final EDAMUserException e) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            EclipseUtil.openErrorSync(shell, Messages.Plugin_Runtime_AuthExpired_Title, Messages.Plugin_Runtime_AuthExpired_Message);
        } else {
            EclipseUtil.openErrorSync(shell, Messages.Plugin_Error_Occurred, e.toString());
        }
    }

}
