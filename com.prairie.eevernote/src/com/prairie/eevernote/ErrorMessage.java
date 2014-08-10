package com.prairie.eevernote;

import com.evernote.edam.error.EDAMNotFoundException;
import com.prairie.eevernote.util.StringUtil;

public class ErrorMessage {

	public static String getMessage(Throwable t) {
		if (t instanceof EDAMNotFoundException) {
			EDAMNotFoundException e = (EDAMNotFoundException) t;
			String args = StringUtil.EMPTY;
			try {
				args = EvernoteDataModel.forName(e.getIdentifier()).toReadableString();
			} catch (IllegalArgumentException expt) {
				// Ignore
			}
			return EEProperties.getProperties().getProperty("EEClipperPlugin.EvernoteException." + EDAMNotFoundException.class.getSimpleName() + ".Message", args);
		}
		return t.getMessage();
	}

	enum EvernoteDataModel {

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

		public static EvernoteDataModel forName(String name) throws IllegalArgumentException {
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
