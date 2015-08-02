package com.prairie.eemory.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.transport.TTransportException;
import com.prairie.eemory.Constants;
import com.prairie.eemory.EemoryPlugin;
import com.prairie.eemory.Messages;
import com.prairie.eemory.client.EeClipper;
import com.prairie.eemory.client.model.ENNote;
import com.prairie.eemory.util.EclipseUtil;
import com.prairie.eemory.util.EncryptionUtil;
import com.prairie.eemory.util.IDialogSettingsUtil;
import com.prairie.eemory.util.LogUtil;

public class ThrowableHandler {

    public static void openError(final Shell shell, final String message) {
        EclipseUtil.openErrorSyncly(shell, Messages.Plugin_Error_Occurred, message);
    }

    public static boolean handleDesignTimeErr(final Shell shell, final Throwable e) {
        return handleDesignTimeErr(shell, e, null);
    }

    private static boolean result;

    public static boolean handleDesignTimeErr(final Shell shell, final Throwable e, final EeClipper clipper) {
        if (e instanceof EDAMUserException) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    result = new EDAMUserExceptionHandler().handleDesingTime(shell, (EDAMUserException) e, clipper);
                }
            });
            return result;
        } else if (e instanceof OutOfDateException) {
            openError(shell, Messages.bind(Messages.Plugin_OutOfDate, EemoryPlugin.getVersion()));
        } else if (e instanceof TTransportException) {
            if (clipper != null) {
                clipper.setInvalid();
            }
            openError(shell, ExceptionUtils.getRootCauseMessage(e));
        } else {
            openError(shell, ExceptionUtils.getRootCauseMessage(e));
        }
        return false;
    }

    public static IStatus handleJobErr(final Throwable e) {
        return handleJobErr(e, null, null, null);
    }

    public static IStatus handleJobErr(final Throwable e, final ENNote args) {
        return handleJobErr(e, null, args, null);
    }

    public static IStatus handleJobErr(final Throwable e, final EeClipper clipper) {
        return handleJobErr(e, clipper, null, null);
    }

    public static IStatus handleJobErr(final Throwable e, final EeClipper clipper, final ENNote args, final Shell shell) {
        if (e instanceof EDAMNotFoundException) {
            if (args != null) {
                return new EDAMNotFoundHandler(EncryptionUtil.decrypt(IDialogSettingsUtil.get(Constants.PLUGIN_SETTINGS_KEY_TOKEN))).fixNotFoundException((EDAMNotFoundException) e, args);
            }
        } else if (e instanceof EDAMUserException) {
            return new EDAMUserExceptionHandler().handleRuntime(shell, (EDAMUserException) e, clipper);
        } else if (e instanceof OutOfDateException) {
            return LogUtil.error(Messages.bind(Messages.Plugin_OutOfDate, EemoryPlugin.getVersion()));
        } else if (e instanceof TTransportException) {
            if (clipper != null) {
                clipper.setInvalid();
            }
        } else if (e instanceof NoDataFoundException) {
            return LogUtil.warning(ExceptionUtils.getRootCauseMessage(e));
        }
        return LogUtil.error(e);
    }

    public static ExecutionException handleExecErr(final Throwable e) {
        return handleExecErr(e, null);
    }

    public static ExecutionException handleExecErr(final Throwable e, final EeClipper clipper) {
        if (e instanceof OutOfDateException) {
            return new ExecutionException(Messages.bind(Messages.Plugin_OutOfDate, EemoryPlugin.getVersion()));
        } else if (e instanceof TTransportException) {
            if (clipper != null) {
                clipper.setInvalid();
            }
        }
        return new ExecutionException(ExceptionUtils.getRootCauseMessage(e), e);
    }

}
