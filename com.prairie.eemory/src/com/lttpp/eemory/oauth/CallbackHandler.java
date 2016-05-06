package com.lttpp.eemory.oauth;

public interface CallbackHandler {

    public abstract void ready() throws Exception;

    public abstract String getCallbackURL();

    public abstract String getVerifier();

    public abstract void done();

}