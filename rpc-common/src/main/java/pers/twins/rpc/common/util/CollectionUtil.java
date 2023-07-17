package pers.twins.rpc.common.util;

import java.util.Collection;

/**
 * @author twins
 * @date 2023-07-17 14:59:13
 */
public class CollectionUtil {

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
