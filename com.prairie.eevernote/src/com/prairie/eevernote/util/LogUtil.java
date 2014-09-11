package com.prairie.eevernote.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.prairie.eevernote.EEPlugin;

public class LogUtil {

    private static final ILog log = EEPlugin.getDefault().getLog();

    public static void logInfo(final Throwable exception) {
        log.log(info(exception));
    }

    public static void logWarning(final Throwable exception) {
        log.log(warning(exception));
    }

    public static void logCancel(final Throwable exception) {
        log.log(cancel(exception));
    }

    public static void logError(final Throwable exception) {
        log.log(error(exception));
    }

    public static IStatus info(final Throwable exception) {
        return new Status(Status.INFO, EEPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus warning(final Throwable exception) {
        return new Status(Status.WARNING, EEPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus cancel(final Throwable exception) {
        return new Status(Status.CANCEL, EEPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus error(final Throwable exception) {
        return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus info(final String message) {
        return new Status(Status.INFO, EEPlugin.PLUGIN_ID, message);
    }

    public static IStatus warning(final String message) {
        return new Status(Status.WARNING, EEPlugin.PLUGIN_ID, message);
    }

    public static IStatus cancel(final String message) {
        return new Status(Status.CANCEL, EEPlugin.PLUGIN_ID, message);
    }
    public static IStatus error(final String message) {
        return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, message);
    }

    public static IStatus ok() {
        return Status.OK_STATUS;
    }

    public static IStatus cancel() {
        return Status.CANCEL_STATUS;
    }

}
