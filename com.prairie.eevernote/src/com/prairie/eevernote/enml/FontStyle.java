package com.prairie.eevernote.enml;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.HTMLUtil;

public enum FontStyle {

    NORMAL {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_NORMAL;
        }

        @Override
        public int toNumber() {
            return ConstantsUtil.ZERO;
        }

    },
    BOLD {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_BOLD;
        }

        @Override
        public int toNumber() {
            return ConstantsUtil.ONE;
        }

    },
    ITALIC {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_ITALIC;
        }

        @Override
        public int toNumber() {
            return ConstantsUtil.TWO;
        }

    },
    BOLD_ITALIC {
        @Override
        public String toString() {
            return HTMLUtil.FONT_STYLE_BOLD_ITALIC;
        }

        @Override
        public int toNumber() {
            return ConstantsUtil.THREE;
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
        throw new IllegalArgumentException(Constants.IllegalArgumentExceptionMsg + code);
    }
}