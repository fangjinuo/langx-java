package com.jn.langx.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

public final class EqualsComparator<V> implements Comparator<V>, Serializable {
    private static final long serialVersionUID = 1L;
    public static final EqualsComparator INSTANCE = new EqualsComparator();


    @Override
    public int compare(V o1, V o2) {
        return o1.equals(o2) ? 0 : o1.hashCode() - o2.hashCode();
    }
}
