package com.prairie.eemory.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;

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

    public static String serialize(final Object object) throws IOException {//TODO effective Java
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
        objectOutput.writeObject(object);
        objectOutput.close();
        return new String(Base64.encodeBase64(byteOutput.toByteArray()));
    }

    public static Object deserialize(final String base64SerializedString) throws IOException, ClassNotFoundException {
        byte[] data = Base64.decodeBase64(base64SerializedString);
        ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(data));
        Object object = objectInput.readObject();
        objectInput.close();
        return object;
    }

}
