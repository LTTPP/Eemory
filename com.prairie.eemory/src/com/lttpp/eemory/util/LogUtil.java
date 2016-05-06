package com.lttpp.eemory.util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.lttpp.eemory.Constants;
import com.lttpp.eemory.EemoryPlugin;

public class LogUtil {

    private static final ILog log = EemoryPlugin.getDefault().getLog();

    public static void debug(final String message) {
        String debug = System.getProperty(Constants.PLUGIN_DEBUG_MODE);
        if (BooleanUtils.toBoolean(debug)) {
            logInfo(message);
        }
    }

    public static void logInfo(final Throwable exception) {
        log.log(info(exception));
    }

    public static void logWarning(final Throwable exception) {
        log.log(warning(exception));
    }

    public static void logWarning(final String message, final Throwable exception) {
        log.log(warning(message, exception));
    }

    public static void logCancel(final Throwable exception) {
        log.log(cancel(exception));
    }

    public static void logError(final Throwable exception) {
        log.log(error(exception));
    }

    public static void logInfo(final String message) {
        log.log(info(message));
    }

    public static void logWarning(final String message) {
        log.log(warning(message));
    }

    public static void logCancel(final String message) {
        log.log(cancel(message));
    }

    public static void logError(final String message) {
        log.log(error(message));
    }

    public static IStatus info(final Throwable exception) {
        return new Status(Status.INFO, EemoryPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus warning(final Throwable exception) {
        return new Status(Status.WARNING, EemoryPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus warning(final String message, final Throwable exception) {
        return new Status(Status.WARNING, EemoryPlugin.PLUGIN_ID, message, exception);
    }

    public static IStatus cancel(final Throwable exception) {
        return new Status(Status.CANCEL, EemoryPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus error(final Throwable exception) {
        return new Status(Status.ERROR, EemoryPlugin.PLUGIN_ID, ExceptionUtils.getRootCauseMessage(exception), exception);
    }

    public static IStatus info(final String message) {
        return new Status(Status.INFO, EemoryPlugin.PLUGIN_ID, message);
    }

    public static IStatus warning(final String message) {
        return new Status(Status.WARNING, EemoryPlugin.PLUGIN_ID, message);
    }

    public static IStatus cancel(final String message) {
        return new Status(Status.CANCEL, EemoryPlugin.PLUGIN_ID, message);
    }
    public static IStatus error(final String message) {
        return new Status(Status.ERROR, EemoryPlugin.PLUGIN_ID, message);
    }

    public static IStatus ok() {
        return Status.OK_STATUS;
    }

    public static IStatus cancel() {
        return Status.CANCEL_STATUS;
    }

}
