package com.prairie.eevernote;

import org.apache.commons.lang3.StringUtils;

import com.evernote.edam.error.EDAMNotFoundException;
import com.prairie.eevernote.exception.EDAMDataModel;

public class ErrorMessage {

    public static String getMessage(final Throwable t) {
        if (t instanceof EDAMNotFoundException) {
            EDAMNotFoundException e = (EDAMNotFoundException) t;
            String args = StringUtils.EMPTY;
            try {
                args = EDAMDataModel.forName(e.getIdentifier()).toReadableString();
            } catch (IllegalArgumentException expt) {
                // Ignore
            }
            return EEProperties.getProperties().getProperty("EEClipperPlugin.EvernoteException." + EDAMNotFoundException.class.getSimpleName() + ".Message", args);
        }
        return t.getMessage();
    }

}
