package com.prairie.eemory.util;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

public class ArrayUtil {

    public static boolean isEqualArray(final Object[] ones, final Object[] others) {
        return isEqualArray(ones, others, false);
    }

    public static boolean isEqualArray(final Object[] ones, final Object[] others, final boolean compareOrder) {
        if (ones == others) {
            return true;
        }
        if (ones == null || others == null) {
            return false;
        }
        if (ones.length != others.length) {
            return false;
        }
        if (compareOrder) {
            for (int i = 0; i < ones.length; i++) {
                if (!ObjectUtil.isEqualObject(ones[i], others[i])) {
                    return false;
                }
            }
        } else {
            List<Integer> matchedIndex = ListUtil.list();
            andContinue: for (Object o1 : ones) {
                for (int i = 0; i < others.length; i++) {
                    if (matchedIndex.contains(i)) {
                        continue;
                    }
                    if (ObjectUtil.isEqualObject(o1, others[i], compareOrder)) {
                        matchedIndex.add(i);
                        break andContinue;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public static <T> boolean isNullArray(final T[] array) {
        return array == null;
    }

    public static <T> boolean isNullOrEmptyArray(final T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> T[] cloneArray(final T[] source) {
        return cloneArray(source, false);
    }

    public static <T> T[] cloneArray(final T[] source, final boolean deep) {
        if (!deep) {
            return ObjectUtils.clone(source);
        }
        if (isNullArray(source)) {
            return null;
        }

        Object clone = null;
        final Class<?> componentType = source.getClass().getComponentType();
        int length = Array.getLength(source);
        clone = Array.newInstance(componentType, length);
        if (componentType.isPrimitive() || componentType == String.class) {
            while (length-- > 0) {
                Array.set(clone, length, Array.get(source, length));
            }
        } else {
            while (length-- > 0) {
                Array.set(clone, length, ObjectUtil.cloneObject(Array.get(source, length), deep));
            }
        }

        @SuppressWarnings("unchecked")
        // OK because input is of type T
        final T[] checkedClone = (T[]) clone;
        return checkedClone;
    }

}
