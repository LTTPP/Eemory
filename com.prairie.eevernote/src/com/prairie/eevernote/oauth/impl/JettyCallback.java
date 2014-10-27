package com.prairie.eevernote.oauth.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.oauth.CallbackHandler;
import com.prairie.eevernote.util.ConstantsUtil;

public class JettyCallback extends AbstractHandler implements CallbackHandler {

    private String verifier;
    private Server server;
    private ServerConnector http;

    public JettyCallback() {
        verifier = StringUtils.EMPTY;
    }

    @Override
    public void ready() throws Exception {
        server = new Server();

        http = new ServerConnector(server);
        http.setHost(ConstantsUtil.LOCALHOST);
        http.setPort(0);
        server.addConnector(http);

        server.setHandler(this);

        server.start();
    }

    @Override
    public String getVerifier() {
        return verifier;
    }

    @Override
    public String getCallbackURL() {
        if (http == null) {
            return StringUtils.EMPTY;
        }
        return ConstantsUtil.url(http.getHost(), String.valueOf(http.getLocalPort()), Constants.CALLBACK_URL, false);
    }

    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (Constants.CALLBACK_URL.equals(target)) {
            verifier = request.getParameter(Constants.OAUTH_VERIFIER);
        }

        response.setContentType(Constants.CALLBACK_HTML_META);
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        PrintWriter doc = response.getWriter();

        if (Constants.CALLBACK_URL.equals(target)) {
            doc.println("<html>");
            doc.println("<body>");
            doc.println("Success! You can close this window now, and go back to Eclipse.");
            doc.println("</body>");
            doc.println("</html>");
            // notify OAuth thread
            synchronized (this) {
                this.notifyAll();
            }
        } else {
            doc.println("<html>");
            doc.println("<body>");
            doc.println("You should not be here.");
            doc.println("</body>");
            doc.println("</html>");
        }
    }

    @Override
    public void dispose() {
        try {
            server.stop();
        } catch (Exception ignored) {
        }
    }

    static {
        try {
            System.setProperty(Constants.JETTY_LOG_IMPL_CLASS, EclipseErrorLog.class.getName());
        } catch (Exception ignored) {
        }
    }

}
