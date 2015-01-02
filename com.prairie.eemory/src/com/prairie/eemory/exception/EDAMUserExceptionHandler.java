package com.prairie.eemory.exception;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMUserException;
import com.prairie.eemory.Constants;
import com.prairie.eemory.Messages;
import com.prairie.eemory.client.EeClipper;
import com.prairie.eemory.oauth.OAuth;
import com.prairie.eemory.util.EclipseUtil;
import com.prairie.eemory.util.EncryptionUtil;
import com.prairie.eemory.util.IDialogSettingsUtil;
import com.prairie.eemory.util.LogUtil;

public class EDAMUserExceptionHandler {

    private boolean reauthorized = false;

    public IStatus handleRuntime(final Shell shell, final EDAMUserException e, final EeClipper clipper) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        oauth(shell);
                    } catch (ExecutionException ignored) {
                    }
                }
            });
            if (reauthorized) {
                if (clipper != null) {
                    clipper.setInvalid();
                }
                return LogUtil.ok();
            }
        }
        return LogUtil.error(e);
    }

    public boolean handleDesingTime(final Shell shell, final EDAMUserException e, final EeClipper clipper) {
        if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
            try {
                oauth(shell);
            } catch (ExecutionException e1) {
                EclipseUtil.openErrorSyncly(shell, Messages.Plugin_Error_Occurred, e.toString());
            }
            if (reauthorized) {
                if (clipper != null) {
                    clipper.setInvalid();
                }
                return true;
            }
        } else {
            EclipseUtil.openErrorSyncly(shell, Messages.Plugin_Error_Occurred, e.toString());
        }
        return false;
    }

    private void oauth(final Shell shell) throws ExecutionException {
        int opt = EclipseUtil.openCustomImageTypeWithCustomButtons(shell, Messages.Plugin_OAuth_AuthExpired_Title, Messages.Plugin_OAuth_AuthExpired_Message, new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream(Constants.OAUTH_EVERNOTE_TRADEMARK_DISCONNECTED)), ArrayUtils.toArray(Messages.Plugin_OAuth_AuthExpired_ReAuth, Messages.Plugin_OAuth_NotNow));
        if (opt == 0) {
            try {
                new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                    @Override
                    public void run(final IProgressMonitor monitor) {
                        monitor.beginTask(Messages.Plugin_OAuth_Waiting, IProgressMonitor.UNKNOWN);
                        try {
                            String token = new OAuth().auth(shell);
                            if (StringUtils.isNotBlank(token)) {
                                IDialogSettingsUtil.set(Constants.PLUGIN_SETTINGS_KEY_TOKEN, EncryptionUtil.encrypt(token));
                                reauthorized = true;
                            }
                        } catch (Throwable e) {
                            ThrowableHandler.handleDesignTimeErr(shell, e);
                        }
                        monitor.done();
                    }
                });
            } catch (Throwable e) {
                throw ThrowableHandler.handleExecErr(e);
            }
        }
    }

}
