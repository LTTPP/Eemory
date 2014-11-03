package com.prairie.eevernote.oauth.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.oauth.CallbackHandler;
import com.prairie.eevernote.util.HttpUtil;

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
        http.setHost(HttpUtil.LOCALHOST);
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
        return HttpUtil.url(http.getHost(), String.valueOf(http.getLocalPort()), Constants.CALLBACK_URL, false);
    }

    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        response.setContentType(Constants.CALLBACK_HTML_META);
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        PrintWriter doc = response.getWriter();

        if (Constants.CALLBACK_URL.equals(target)) {
            try {
                verifier = request.getParameter(Constants.OAUTH_VERIFIER);
                if (StringUtils.isNotBlank(verifier)) {
                    doc.println(FileUtils.readFileToString(FileUtils.toFile(FileLocator.resolve(getClass().getResource("html/callback.html"))), CharEncoding.UTF_8));
                } else {
                    doc.println(FileUtils.readFileToString(FileUtils.toFile(FileLocator.resolve(getClass().getResource("html/callback_err.html"))), CharEncoding.UTF_8));
                }
            } finally {
                // notify OAuth thread
                synchronized (this) {
                    notifyAll();
                }
            }
        } else {
            doc.println(FileUtils.readFileToString(FileUtils.toFile(FileLocator.resolve(getClass().getResource("html/nottarget.html"))), CharEncoding.UTF_8));
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
