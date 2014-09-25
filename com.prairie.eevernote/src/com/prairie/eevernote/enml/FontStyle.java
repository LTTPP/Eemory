package com.prairie.eevernote.enml;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.Messages;
import com.prairie.eevernote.util.HTMLUtil;

public enum FontStyle {

    NORMAL {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_NORMAL;
        }

        @Override
        public int toNumber() {
            return 0;
        }

    },
    BOLD {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_BOLD;
        }

        @Override
        public int toNumber() {
            return 1;
        }

    },
    ITALIC {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_ITALIC;
        }

        @Override
        public int toNumber() {
            return 2;
        }

    },
    BOLD_ITALIC {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_BOLD_ITALIC;
        }

        @Override
        public int toNumber() {
            return 3;
        }

    };

    public abstract int toNumber();

    public static FontStyle forNumber(final int code) throws IllegalArgumentException {
        FontStyle[] values = FontStyle.values();
        for (FontStyle value : values) {
            if (value.toNumber() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException(Messages.getString(Constants.ILLEGAL_ARGUMENT_EXCEPTION_MSG) + code);
    }
}