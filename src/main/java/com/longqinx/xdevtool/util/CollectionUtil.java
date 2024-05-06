package com.longqinx.xdevtool.util;

import java.util.Collection;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class CollectionUtil {
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}
