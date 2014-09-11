package com.prairie.eevernote.exception;

public enum EvernoteDataModel {

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