package com.prairie.eemory.oauth.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.prairie.eemory.Constants;
import com.prairie.eemory.oauth.CallbackHandler;
import com.prairie.eemory.util.HttpUtil;

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

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(FileLocator.resolve(getClass().getResource(Constants.OAUTH_RESOURCE_BASE)).getPath());
        resourceHandler.setWelcomeFiles(ArrayUtils.toArray(Constants.OAUTH_DEFAULT_HTML));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(ArrayUtils.toArray(this, resourceHandler, new DefaultHandler()));
        server.setHandler(handlers);

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
        if (Constants.CALLBACK_URL.equals(target)) {
            try {
                response.setContentType(Constants.CALLBACK_HTML_META);
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                PrintWriter doc = response.getWriter();

                verifier = request.getParameter(Constants.OAUTH_VERIFIER);
                if (StringUtils.isNotBlank(verifier)) {
                    doc.println(FileUtils.readFileToString(FileUtils.toFile(FileLocator.resolve(getClass().getResource(Constants.OAUTH_CALLBACK_HTML))), CharEncoding.UTF_8));
                } else {
                    doc.println(FileUtils.readFileToString(FileUtils.toFile(FileLocator.resolve(getClass().getResource(Constants.OAUTH_CALLBACK_ERR_HTML))), CharEncoding.UTF_8));
                }
            } finally {
                // notify OAuth thread verifier got, or error occurred. requests maybe not all handled here.
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }

    @Override
    public void done() {
        try {
            // server.stop(); // theoretically we can not stop server here. but we don't know where else to stop.
        } catch (Exception ignored) {
        }
    }

    static {
        try {
            System.setProperty(Constants.JETTY_LOG_IMPL_CLASS, EclipseErrorLog.class.getName());
        } catch (Exception ignored) {
        }
    }

    private class DefaultHandler extends AbstractHandler {
        @Override
        public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            if (baseRequest.isHandled()) {
                return;
            }
            response.setContentType(Constants.CALLBACK_HTML_META);
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            PrintWriter doc = response.getWriter();
            doc.println(FileUtils.readFileToString(FileUtils.toFile(FileLocator.resolve(getClass().getResource(Constants.OAUTH_NOTTARGET_HTML))), CharEncoding.UTF_8));
        }
    }

}
