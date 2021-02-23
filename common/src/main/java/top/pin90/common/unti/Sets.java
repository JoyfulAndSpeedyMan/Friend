package top.pin90.common.unti;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Sets {
    public static <T> Set<T> of(T... elements) {
        return Arrays.stream(elements)
                .collect(Collectors.toSet());
    }
}
