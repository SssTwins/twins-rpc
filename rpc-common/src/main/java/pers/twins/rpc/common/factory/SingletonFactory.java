package pers.twins.rpc.common.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton object construction factory
 *
 * @author twins
 * @date 2023-07-16 22:52:58
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SingletonFactory {

    private static final Map<String, Object> OBJECTS = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> c) {
        if (Objects.isNull(c)) {
            throw new IllegalArgumentException();
        }
        String key = c.toString();
        if (OBJECTS.containsKey(key)) {
            return c.cast(OBJECTS.get(key));
        } else {
            return c.cast(OBJECTS.computeIfAbsent(key, k -> {
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }
}
