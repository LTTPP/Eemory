package com.prairie.eevernote.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMUserException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.Messages;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.LogUtil;

public class EDAMUserExceptionHandler {

    public IStatus handleRuntime(final EDAMUserException e) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, Messages.getString(Constants.PLUGIN_RUNTIME_AUTHEXPIRED_MESSAGE));
        }
        return LogUtil.error(e);
    }

    public void handleDesingTime(final Shell shell, final EDAMUserException e) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            EclipseUtil.openErrorSync(shell, Messages.getString(Constants.PLUGIN_RUNTIME_AUTHEXPIRED_TITLE), Messages.getString(Constants.PLUGIN_RUNTIME_AUTHEXPIRED_MESSAGE));
        } else {
            EclipseUtil.openErrorSync(shell, Messages.getString(Constants.PLUGIN_ERROR_OCCURRED), e.toString());
        }
    }

}
