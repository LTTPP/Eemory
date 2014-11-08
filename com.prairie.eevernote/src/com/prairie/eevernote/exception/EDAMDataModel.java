package com.prairie.eevernote.exception;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.Messages;

public enum EDAMDataModel {

    Note_noteGuid {
        @Override
        public String toString() {
            return Constants.ENML_MODEL_NOTE_NOTEGUID;
        }

        @Override
        public String toReadableString() {
            return Constants.ENML_MODEL_NOTE_NOTEGUID_READABLE;
        }
    },

    Note_notebookGuid {
        @Override
        public String toString() {
            return Constants.ENML_MODEL_NOTE_NOTEBOOKGUID;
        }

        @Override
        public String toReadableString() {
            return Constants.ENML_MODEL_NOTE_NOTEBOOKGUID_READABLE;
        }
    };

    public abstract String toReadableString();

    public static EDAMDataModel forName(final String name) throws IllegalArgumentException {
        EDAMDataModel[] values = EDAMDataModel.values();
        for (EDAMDataModel value : values) {
            if (value.toString().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException(Messages.Throwable_IllegalArgumentException_Message + name);
    }
}