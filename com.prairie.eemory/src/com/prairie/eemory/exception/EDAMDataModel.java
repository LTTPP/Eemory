package com.prairie.eemory.exception;

import com.prairie.eemory.Constants;
import com.prairie.eemory.Messages;

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
        throw new IllegalArgumentException(Messages.bind(Messages.Throwable_IllegalArgumentException_Message, name));
    }
}