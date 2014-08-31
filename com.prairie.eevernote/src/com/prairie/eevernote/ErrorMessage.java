package com.prairie.eevernote;

import org.apache.commons.lang3.StringUtils;

import com.evernote.edam.error.EDAMNotFoundException;

public class ErrorMessage {

    public static String getMessage(final Throwable t) {
        if (t instanceof EDAMNotFoundException) {
            EDAMNotFoundException e = (EDAMNotFoundException) t;
            String args = StringUtils.EMPTY;
            try {
                args = EvernoteDataModel.forName(e.getIdentifier()).toReadableString();
            } catch (IllegalArgumentException expt) {
                // Ignore
            }
            return EEProperties.getProperties().getProperty("EEClipperPlugin.EvernoteException." + EDAMNotFoundException.class.getSimpleName() + ".Message", args);
        }
        return t.getMessage();
    }

    public enum EvernoteDataModel {

        Note_notebookGuid {
            @Override
            public String toString() {
                return "Note.notebookGuid";
            }

            @Override
            public String toReadableString() {
                return "Notebook";
            }
        };

        public abstract String toReadableString();

        public static EvernoteDataModel forName(final String name) throws IllegalArgumentException {
            EvernoteDataModel[] values = EvernoteDataModel.values();
            for (EvernoteDataModel value : values) {
                if (value.toString().equalsIgnoreCase(name)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Illegal Enum name: " + name);
        }
    }

}
