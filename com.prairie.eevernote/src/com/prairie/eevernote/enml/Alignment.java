package com.prairie.eevernote.enml;

public enum Alignment {

	LEFT {
		@Override
		public String toString() {
			return "left";
		}

		@Override
		public int toNumber() {
			return 1;
		}

	},
	CENTER {
		@Override
		public String toString() {
			return "center";
		}

		@Override
		public int toNumber() {
			return 2;
		}

	},
	RIGHT {
		@Override
		public String toString() {
			return "right";
		}

		@Override
		public int toNumber() {
			return 3;
		}

	};

	public abstract int toNumber();

	public static Alignment forNumber(int code) throws IllegalArgumentException {
		Alignment[] values = Alignment.values();
		for (Alignment value : values) {
			if (value.toNumber() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Illegal alignment code: " + code);
	}
}