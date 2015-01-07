package com.prairie.eemory.oauth;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.evernote.auth.EvernoteService;
import com.prairie.eemory.Constants;
import com.prairie.eemory.Messages;
import com.prairie.eemory.oauth.impl.JettyCallback;
import com.prairie.eemory.util.ClipboardUtil;
import com.prairie.eemory.util.SyncEclipseUtil;
import com.prairie.eemory.util.EncryptionUtil;
import com.prairie.eemory.util.EvernoteUtil;

public class OAuth {

    private static final String CONSUMER_KEY = "eevernote";
    private static final String CONSUMER_SECRET = "kmLkG5Z1dFnBAam5oqie9NgHqN2zfojd+lg/00GRroU=";

    private final CallbackHandler callback;

    public OAuth() throws Exception {
        callback = new JettyCallback();
        callback.ready();
    }

    public String auth(final Shell shell) throws MalformedURLException, InterruptedException {
        try {
            Class<? extends EvernoteApi> apiClass = EvernoteUtil.evernoteService() == EvernoteService.PRODUCTION ? EvernoteApi.class : EvernoteApi.Sandbox.class;
            OAuthService service = new ServiceBuilder().provider(apiClass).apiKey(CONSUMER_KEY).apiSecret(EncryptionUtil.decrypt(CONSUMER_SECRET)).callback(callback.getCallbackURL()).build();
            Token requestToken = service.getRequestToken();
            String authUrl = service.getAuthorizationUrl(requestToken);
            try {
                PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(authUrl));
            } catch (PartInitException couldNotOpenBrowser) {
                int opt = new SyncEclipseUtil().openCustomImageTypeWithCustomButtonsSyncly(shell, Messages.Plugin_OAuth_Title, Messages.Plugin_OAuth_DoItManually, new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream(Constants.OAUTH_EVERNOTE_TRADEMARK)), ArrayUtils.toArray(Messages.Plugin_OAuth_Copy, Messages.Plugin_OAuth_Cancel));
                if (opt == 0) {
                    ClipboardUtil.copy(authUrl);
                } else {
                    return StringUtils.EMPTY;
                }
            }

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
            callback.done();
        }
    }

}
