package com.prairie.eevernote.enml;

public enum FontStyle {

	NORMAL {
		@Override
		public String toString() {
			return "normal";
		}

		@Override
		public int toNumber() {
			return 0;
		}

	},
	BOLD {
		@Override
		public String toString() {
			return "bold";
		}

		@Override
		public int toNumber() {
			return 1;
		}

	},
	ITALIC {
		@Override
		public String toString() {
			return "italic";
		}

		@Override
		public int toNumber() {
			return 2;
		}

	};

	public abstract int toNumber();

	public static FontStyle forNumber(int code) throws IllegalArgumentException {
		FontStyle[] values = FontStyle.values();
		for (FontStyle value : values) {
			if (value.toNumber() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Illegal font style code: " + code);
	}
}