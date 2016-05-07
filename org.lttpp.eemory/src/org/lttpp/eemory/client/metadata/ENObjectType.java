package org.lttpp.eemory.client.metadata;

import org.lttpp.eemory.Constants;
import org.lttpp.eemory.Messages;
import org.lttpp.eemory.util.LogUtil;

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
        LogUtil.debug(Messages.bind(Messages.Throwable_IllegalArgumentException_Message, name));
        throw new IllegalArgumentException(Messages.bind(Messages.Throwable_IllegalArgumentException_Message, name));
    }

}