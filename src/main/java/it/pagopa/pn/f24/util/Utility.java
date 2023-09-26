package it.pagopa.pn.f24.util;

import it.pagopa.pn.f24.dto.F24Type;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Utility {
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * Returns the first index of an element that matches the predicate
     * or -1 if there are no matching elements
     */
    public static <T> Integer getIndexOfByPredicate(List<T> list, Predicate<Integer> matcher) {
        return IntStream.range(0, list.size())
                .filter(matcher::test)
                .findFirst()
                .orElse(INDEX_NOT_FOUND);
    }

    public static <T> Integer countElementsByPredicate(List<T> list, Predicate<T> matcher) {
        return Math.toIntExact(list.stream()
                .filter(matcher)
                .count());
    }

    public static F24Type getF24TypeFromMetadata(F24Metadata f24Metadata) {
        if(f24Metadata.getF24Standard() != null) {
            return F24Type.F24_STANDARD;
        } else if(f24Metadata.getF24Simplified() != null) {
            return F24Type.F24_SIMPLIFIED;
        } else if(f24Metadata.getF24Elid() != null) {
            return F24Type.F24_ELID;
        } else if(f24Metadata.getF24Excise() != null) {
            return F24Type.F24_EXCISE;
        }

        throw new RuntimeException("Invalid F24 Type");
    }

    public static String convertPathTokensList(List<String> pathTokens) {
        return String.join("_", pathTokens);
    }
}
