package com.prairie.eemory.oauth.impl;

import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

import com.prairie.eemory.util.LogUtil;

public class EclipseErrorLog extends AbstractLogger {

    private final String name;

    public EclipseErrorLog() {
        this(EclipseErrorLog.class.getName());
    }

    public EclipseErrorLog(final String name) {
        this.name = name;
    }

    @Override
    public void debug(final Throwable thrown) {
        nop();
    }

    @Override
    public void debug(final String message, final Object... args) {
        nop();
    }

    @Override
    public void debug(final String message, final Throwable args) {
        nop();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void ignore(final Throwable thrown) {
        nop();
    }

    @Override
    public void info(final Throwable thrown) {
        nop();
    }

    @Override
    public void info(final String message, final Object... args) {
        nop();
    }

    @Override
    public void info(final String message, final Throwable args) {
        nop();
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void setDebugEnabled(final boolean debugEnabled) {
        nop();
    }

    @Override
    public void warn(final Throwable thrown) {
        LogUtil.logWarning(thrown);
    }

    @Override
    public void warn(final String message, final Object... args) {
        LogUtil.logWarning(format(message, args));
    }

    @Override
    public void warn(final String message, final Throwable thrown) {
        LogUtil.logWarning(message, thrown);
    }

    @Override
    protected Logger newLogger(final String name) {
        return new EclipseErrorLog(name);
    }

    private String format(String msg, final Object... args) {
        msg = String.valueOf(msg); // Avoids NPE
        String braces = "{}";
        StringBuilder builder = new StringBuilder();
        int start = 0;
        for (Object arg : args) {
            int bracesIndex = msg.indexOf(braces, start);
            if (bracesIndex < 0) {
                builder.append(msg.substring(start));
                builder.append(" ");
                builder.append(arg);
                start = msg.length();
            } else {
                builder.append(msg.substring(start, bracesIndex));
                builder.append(String.valueOf(arg));
                start = bracesIndex + braces.length();
            }
        }
        builder.append(msg.substring(start));
        return builder.toString();
    }

    private void nop() {

    }

}
