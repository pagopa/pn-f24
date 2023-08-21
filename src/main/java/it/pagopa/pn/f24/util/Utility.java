package it.pagopa.pn.f24.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Utility {
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * Returns the first index of an element that matches the predicate
     * or -1 if there are no matching elements
     * @param list
     * @param matcher
     * @return
     * @param <T>
     */
    public static <T> Integer getIndexOfByPredicate(List<T> list, Predicate<Integer> matcher) {
        return IntStream.range(0, list.size())
                .filter(matcher::test)
                .findFirst()
                .orElse(INDEX_NOT_FOUND);
    }

    public static <T> String objectToJsonString(T object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            //TODO Gestire eccezione
            throw new RuntimeException(e);
        }
    }
}
