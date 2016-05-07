package org.lttpp.eemory.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.Messages;

public class ObjectUtil {

    public static boolean isEqualObject(final Object one, final Object other) {
        return isEqualObject(one, other, false);
    }

    public static boolean isEqualObject(final Object one, final Object other, final boolean compareOrder) {
        if (one == other) {
            return true;
        }
        if (one == null || other == null) {
            return false;
        }
        if (one instanceof Map && other instanceof Map) {
            return MapUtil.isEqualList((Map<?, ?>) one, (Map<?, ?>) other);
        }
        if (one instanceof List && other instanceof List) {
            return ListUtil.isEqualList((List<?>) one, (List<?>) other, compareOrder);
        }
        if (one instanceof Object[] && other instanceof Object[]) {
            return ArrayUtil.isEqualArray((Object[]) one, (Object[]) other);
        }
        return one.equals(other);
    }

    public static <T> T cloneObject(final T obj) {
        return cloneObject(obj, false);
    }

    public static <T> T cloneObject(final T obj, final boolean deep) {
        if (!deep) {
            return ObjectUtils.clone(obj);
        }
        if (obj == null) {
            return null;
        }

        Object clone = null;
        if (obj instanceof ArrayList) {
            clone = ListUtil.cloneList((ArrayList<?>) obj, deep);
        } else if (obj instanceof HashMap) {
            clone = MapUtil.cloneMap((HashMap<?, ?>) obj, deep);
        } else if (obj.getClass().isArray()) {
            clone = ArrayUtil.cloneArray((Object[]) obj, deep);
        } else if (!(obj instanceof Cloneable)) {
            clone = cloneCloneNotSupportedObject(obj, deep);
        } else {
            // should not be an Array
            clone = ObjectUtils.clone(obj);
        }

        @SuppressWarnings("unchecked")
        // OK because input is of type T
        final T checkedClone = (T) clone;
        return checkedClone;
    }

    @SuppressWarnings("unchecked")
    protected static <T> T cloneCloneNotSupportedObject(final T obj, final boolean deep) {
        if (obj instanceof String) {
            return obj;
        } else if (obj instanceof Byte) {
            return (T) new Byte((Byte) obj);
        } else if (obj instanceof Short) {
            return (T) new Short((Short) obj);
        } else if (obj instanceof Integer) {
            return (T) new Integer((Integer) obj);
        } else if (obj instanceof Long) {
            return (T) new Long((Long) obj);
        } else if (obj instanceof Float) {
            return (T) new Float((Float) obj);
        } else if (obj instanceof Double) {
            return (T) new Double((Double) obj);
        } else if (obj instanceof Boolean) {
            return (T) new Boolean((Boolean) obj);
        } else if (obj instanceof Character) {
            return (T) new Character((Character) obj);
        }
        return null;
    }

    /*
     * Assume object is serializable.
     */
    public static String serialize(final Object object) {
        if (!(object instanceof Serializable)) {
            throw new SerializationException(Messages.bind(Messages.Throwable_NotSerializable_Message, object));
        }
        byte[] bytes = SerializationUtils.serialize((Serializable) object);
        return new String(Base64.encodeBase64(bytes));
    }

    public static Object deserialize(final String base64SerializedString) {
        byte[] bytes = Base64.decodeBase64(base64SerializedString);
        return SerializationUtils.deserialize(bytes);
    }

    public static String toString(final Object obj) {
        return obj == null ? StringUtils.EMPTY : obj.toString();
    }

}
