package com.lttpp.eemory.client.metadata;

import org.scribe.builder.api.EvernoteApi;

import com.evernote.auth.EvernoteService;
import com.lttpp.eemory.Constants;

public enum EvernoteBrand {

    EVERNOTE_INTERNATIONAL {
        @Override
        public String brandName() {
            return Constants.EVERNOTE_INTERNATIONAL;
        }

        @Override
        public EvernoteService service() {
            return EvernoteService.PRODUCTION;
        }

        @Override
        public Class<? extends EvernoteApi> scribeOAuthApi() {
            return EvernoteApi.class;
        }
    },
    EVERNOTE_YINXIANG {
        @Override
        public String brandName() {
            return Constants.EVERNOTE_YINXIANG;
        }

        @Override
        public EvernoteService service() {
            return EvernoteService.YINXIANG;
        }

        @Override
        public Class<? extends EvernoteApi> scribeOAuthApi() {
            return EvernoteApi.Yinxiang.class;
        }
    },
    EVERNOTE_SANDBOX {
        @Override
        public String brandName() {
            return Constants.EVERNOTE_SANDBOX;
        }

        @Override
        public EvernoteService service() {
            return EvernoteService.SANDBOX;
        }

        @Override
        public Class<? extends EvernoteApi> scribeOAuthApi() {
            return EvernoteApi.Sandbox.class;
        }
    };

    public abstract String brandName();
    public abstract EvernoteService service();

    public abstract Class<? extends EvernoteApi> scribeOAuthApi();

    public static EvernoteBrand forBrandName(final String brandName) {
        EvernoteBrand[] brands = EvernoteBrand.values();
        for (EvernoteBrand brand : brands) {
            if (brand.brandName().equalsIgnoreCase(brandName)) {
                return brand;
            }
        }
        return null;
    }

    public static EvernoteBrand forService(final EvernoteService service) {
        EvernoteBrand[] brands = EvernoteBrand.values();
        for (EvernoteBrand brand : brands) {
            if (brand.service() == service) {
                return brand;
            }
        }
        return null;
    }

}
