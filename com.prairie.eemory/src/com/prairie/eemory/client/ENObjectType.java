package com.prairie.eemory.client;

import com.prairie.eemory.Constants;
import com.prairie.eemory.Messages;

public enum ENObjectType {

    NORMAL {
        @Override
        public String toString() {
            return Constants.ENOBJECT_TYPE_NORMAL;
        }
    },
    LINKED {
        @Override
        public String toString() {
            return Constants.ENOBJECT_TYPE_LINKED;
        }
    },
    BUSINESS {
        @Override
        public String toString() {
            return Constants.ENOBJECT_TYPE_BUSINESS;
        }
    };

    public static ENObjectType forName(final String name) throws IllegalArgumentException {
        ENObjectType[] values = ENObjectType.values();
        for (ENObjectType value : values) {
            if (value.toString().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException(Messages.bind(Messages.Throwable_IllegalArgumentException_Message, name));
    }

}