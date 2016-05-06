package com.lttpp.eemory.exception;

import java.util.LinkedHashMap;

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
import com.lttpp.eemory.Constants;
import com.lttpp.eemory.Messages;
import com.lttpp.eemory.client.EeClipper;
import com.lttpp.eemory.oauth.OAuth;
import com.lttpp.eemory.util.EclipseUtil;
import com.lttpp.eemory.util.EncryptionUtil;
import com.lttpp.eemory.util.EvernoteUtil;
import com.lttpp.eemory.util.IDialogSettingsUtil;
import com.lttpp.eemory.util.LogUtil;
import com.lttpp.eemory.util.MapUtil;

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
        LinkedHashMap<String, String> btns = MapUtil.orderedMap();
        btns.put(Constants.Plugin_OAuth_AuthExpired_ReAuth, Messages.Plugin_OAuth_AuthExpired_ReAuth);
        btns.put(Constants.Plugin_OAuth_NotNow, Messages.Plugin_OAuth_NotNow);

        String opt = EclipseUtil.openCustomImageTypeWithCustomButtons(shell, Messages.Plugin_OAuth_AuthExpired_Title, Messages.bind(Messages.Plugin_OAuth_AuthExpired_Message, EvernoteUtil.brand().brandName()), new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream(Constants.OAUTH_EVERNOTE_TRADEMARK_DISCONNECTED)), btns);
        if (Constants.Plugin_OAuth_AuthExpired_ReAuth.equals(opt)) {
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
