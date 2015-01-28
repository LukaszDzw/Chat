package pl.umk.chat.utils;

import java.util.Map;

/**
 * Created by Lukasz on 2015-01-27.
 */
public class HashMapHelper {
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
