package org.lttpp.eemory.dom;

@SuppressWarnings("serial")
public class FactoryConfigurationError extends Error {

    /**
     * <code>Exception</code> that represents the error.
     */
    private final Exception exception;

    /**
     * Create a new <code>FactoryConfigurationError</code> with no detail
     * mesage.
     */

    public FactoryConfigurationError() {
        super();
        exception = null;
    }

    /**
     * Create a new <code>FactoryConfigurationError</code> with the
     * <code>String </code> specified as an error message.
     * 
     * @param msg
     *            The error message for the exception.
     */

    public FactoryConfigurationError(final String msg) {
        super(msg);
        exception = null;
    }

    /**
     * Create a new <code>FactoryConfigurationError</code> with a given
     * <code>Exception</code> base cause of the error.
     * 
     * @param e
     *            The exception to be encapsulated in a
     *            FactoryConfigurationError.
     */

    public FactoryConfigurationError(final Exception e) {
        super(e.toString());
        exception = e;
    }

    /**
     * Create a new <code>FactoryConfigurationError</code> with the given
     * <code>Exception</code> base cause and detail message.
     * 
     * @param e
     *            The exception to be encapsulated in a
     *            FactoryConfigurationError
     * @param msg
     *            The detail message.
     */

    public FactoryConfigurationError(final Exception e, final String msg) {
        super(msg);
        exception = e;
    }

    /**
     * Return the message (if any) for this error . If there is no message for
     * the exception and there is an encapsulated exception then the message of
     * that exception, if it exists will be returned. Else the name of the
     * encapsulated exception will be returned.
     * 
     * @return The error message.
     */

    @Override
    public String getMessage() {
        String message = super.getMessage();

        if (message == null && exception != null) {
            return exception.getMessage();
        }

        return message;
    }

    /**
     * Return the actual exception (if any) that caused this exception to be
     * raised.
     * 
     * @return The encapsulated exception, or null if there is none.
     */

    public Exception getException() {
        return exception;
    }

    /**
     * use the exception chaining mechanism of JDK1.4
     */
    @Override
    public Throwable getCause() {
        return exception;
    }
}
