package com.prairie.eevernote.exception;

public enum EDAMDataModel {

    Note_noteGuid {
        @Override
        public String toString() {
            return "Note.guid";
        }

        @Override
        public String toReadableString() {
            return "Note";
        }
    },

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

    public static EDAMDataModel forName(final String name) throws IllegalArgumentException {
        EDAMDataModel[] values = EDAMDataModel.values();
        for (EDAMDataModel value : values) {
            if (value.toString().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Illegal Enum name: " + name);
    }
}