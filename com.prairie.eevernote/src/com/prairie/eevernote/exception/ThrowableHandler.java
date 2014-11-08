package com.prairie.eevernote.exception;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Shell;

import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.transport.TTransportException;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.Messages;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.LogUtil;

public class ThrowableHandler {

    public static void openError(final Shell shell, final String message) {
        EclipseUtil.openErrorSync(shell, Messages.Plugin_Error_Occurred, message);
    }

    public static void handleDesignTimeErr(final Shell shell, final Throwable e) {
        handleDesignTimeErr(shell, e, false, null);
    }

    public static void handleDesignTimeErr(final Shell shell, final Throwable e, final boolean fatal) {
        handleDesignTimeErr(shell, e, fatal, null);
    }

    public static void handleDesignTimeErr(final Shell shell, final Throwable e, final EEClipper clipper) {
        handleDesignTimeErr(shell, e, false, clipper);
    }

    public static void handleDesignTimeErr(final Shell shell, final Throwable e, final boolean fatal, final EEClipper clipper) {
        if (e instanceof EDAMUserException) {
            new EDAMUserExceptionHandler().handleDesingTime(shell, (EDAMUserException) e);
        } else if (e instanceof OutOfDateException) {
            openError(shell, EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + Messages.Plugin_Runtime_AddFileToEvernote_OutOfDate);
        } else if (e instanceof TTransportException) {
            if (clipper != null) {
                clipper.setInvalid();
            }
            openError(shell, ExceptionUtils.getRootCauseMessage(e) + (fatal ? StringUtils.EMPTY : Messages.Plugin_Throwable_NotFatal));
        } else {
            openError(shell, ExceptionUtils.getRootCauseMessage(e) + (fatal ? StringUtils.EMPTY : Messages.Plugin_Throwable_NotFatal));
        }
    }

    public static IStatus handleJobErr(final Throwable e) {
        return handleJobErr(e, null);
    }

    public static IStatus handleJobErr(final Throwable e, final EEClipper clipper) {
        if (e instanceof EDAMUserException) {
            return new EDAMUserExceptionHandler().handleRuntime((EDAMUserException) e);
        } else if (e instanceof OutOfDateException) {
            return LogUtil.error(EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + Messages.Plugin_Runtime_AddFileToEvernote_OutOfDate);
        } else if (e instanceof TTransportException) {
            if (clipper != null) {
                clipper.setInvalid();
            }
            return LogUtil.error(e);
        } else {
            return LogUtil.error(e);
        }
    }

    public static ExecutionException handleExecErr(final Throwable e) {
        return handleExecErr(e, null);
    }

    public static ExecutionException handleExecErr(final Throwable e, final EEClipper clipper) {
        if (e instanceof OutOfDateException) {
            return new ExecutionException(EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + Messages.Plugin_Runtime_AddFileToEvernote_OutOfDate);
        } else if (e instanceof TTransportException) {
            if (clipper != null) {
                clipper.setInvalid();
            }
            return new ExecutionException(ExceptionUtils.getRootCauseMessage(e), e);
        } else {
            return new ExecutionException(ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

}
