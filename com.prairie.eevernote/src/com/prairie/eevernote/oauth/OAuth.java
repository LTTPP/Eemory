package com.prairie.eevernote.oauth;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.evernote.auth.EvernoteService;
import com.prairie.eevernote.oauth.impl.JettyCallback;
import com.prairie.eevernote.util.EncryptionUtil;
import com.prairie.eevernote.util.EvernoteUtil;

public class OAuth {

    private static final String CONSUMER_KEY = "eevernote";
    private static final String CONSUMER_SECRET = "kmLkG5Z1dFnBAam5oqie9NgHqN2zfojd+lg/00GRroU=";

    private final CallbackHandler callback;

    public OAuth() throws Exception {
        callback = new JettyCallback();
        callback.ready();
    }

    public String auth() throws PartInitException, MalformedURLException, InterruptedException {
        try {
            Class<? extends EvernoteApi> apiClass = EvernoteUtil.evernoteService() == EvernoteService.PRODUCTION ? EvernoteApi.class : EvernoteApi.Sandbox.class;
            OAuthService service = new ServiceBuilder().provider(apiClass).apiKey(CONSUMER_KEY).apiSecret(EncryptionUtil.decrypt(CONSUMER_SECRET)).callback(callback.getCallbackURL()).build();
            Token requestToken = service.getRequestToken();
            String authUrl = service.getAuthorizationUrl(requestToken);
            PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(authUrl));

            // wait for callback handling
            synchronized (callback) {
                callback.wait(30 * 60 * 1000);// 30 minutes
            }

            String verifierValue = callback.getVerifier();
            if (StringUtils.isBlank(verifierValue)) {
                return StringUtils.EMPTY;
            }
            Verifier verifier = new Verifier(verifierValue);
            Token accessToken = service.getAccessToken(requestToken, verifier);
            return accessToken.getToken();
        } finally {
            callback.dispose();
        }
    }

}
