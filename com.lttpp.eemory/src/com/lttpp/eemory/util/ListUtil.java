package com.lttpp.eemory.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

public final class ListUtil {

    public static String[] toStringArray(final Collection<String> list) {
        if (list == null) {
            return null;
        }
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public static String[] toStringArray(final List<?> list, final ListStringizer stringizer) {
        if (list == null) {
            return null;
        }
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = stringizer.element(list.get(i));
        }
        return array;
    }

    public static List<String> toStringList(final List<?> list, final ListStringizer stringizer) {
        if (list == null) {
            return null;
        }
        List<String> strList = list();
        for (int i = 0; i < list.size(); i++) {
            strList.add(stringizer.element(list.get(i)));
        }
        return strList;
    }

    public static Map<String, String> toStringMap(final List<?> list, final MapStringizer stringizer) {
        if (list == null) {
            return null;
        }
        Map<String, String> map = MapUtil.map();
        for (int i = 0; i < list.size(); i++) {
            map.put(stringizer.key(list.get(i)), stringizer.value(list.get(i)));
        }
        return map;
    }

    public static <T> List<T> toList(final T[] array) {
        List<T> l = list(array.length);
        l.addAll(Arrays.asList(array));
        return l;
    }

    public static <T> List<T> list(final int initialCapacity) {
        return new ArrayList<T>(initialCapacity);
    }

    public static <T> List<T> list() {
        return new ArrayList<T>();
    }

    @SafeVarargs
    public static <T> List<T> list(final T... objects) {
        List<T> l = list();
        l.addAll(toList(objects));
        return l;
    }

    public static boolean isNullOrEmptyList(final List<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNullList(final Collection<?> list) {
        return list == null;
    }

    public static <T> boolean isIndexOutOfBounds(final List<T> list, final int index) {
        return isNullList(list) || index >= 0 && index < list.size();
    }

    public static <T> void replace(final List<T> list, final T newElement, final int index) {
        if (!isIndexOutOfBounds(list, index)) {
            list.remove(index);
            list.add(index, newElement);
        }
    }

    public static boolean isEqualList(final List<?> one, final List<?> other) {
        return isEqualList(one, other, false);
    }

    public static boolean isEqualList(final List<?> one, final List<?> other, final boolean compareOrder) {
        if (one == other) {
            return true;
        }
        if (isNullList(one) || isNullList(other)) {
            return false;
        }
        if (one.size() != other.size()) {
            return false;
        }
        if (compareOrder) {
            Iterator<?> iter1 = one.iterator();
            Iterator<?> iter2 = other.iterator();
            while (iter1.hasNext() && iter2.hasNext()) {
                if (!ObjectUtil.isEqualObject(iter1.next(), iter2.next())) {
                    return false;
                }
            }
        } else {
            List<Integer> matchedIndex = ListUtil.list();
            andContinue: for (Object o1 : one) {
                for (int i = 0; i < other.size(); i++) {
                    if (matchedIndex.contains(i)) {
                        continue;
                    }
                    if (ObjectUtil.isEqualObject(o1, other.get(i), compareOrder)) {
                        matchedIndex.add(i);
                        break andContinue;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public static <E> List<E> cloneList(final ArrayList<E> source) {
        return cloneList(source, false);
    }

    public static <E> List<E> cloneList(final ArrayList<E> source, final boolean deep) {
        if (!deep) {
            return ObjectUtils.clone(source);
        }
        if (isNullList(source)) {
            return null;
        }
        List<E> list = list(source.size());
        for (E e : source) {
            list.add(ObjectUtil.cloneObject(e, deep));
        }
        return list;
    }

}
