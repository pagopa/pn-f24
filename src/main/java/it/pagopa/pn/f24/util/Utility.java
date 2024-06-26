package it.pagopa.pn.f24.util;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.f24.dto.F24Type;
import it.pagopa.pn.f24.generated.openapi.server.v1.dto.F24Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Utility {
    private Utility() { }

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

        throw new PnInternalException("Invalid F24 Type", "ERROR_INVALID_F24_TYPE");
    }

    public static String convertPathTokensList(List<String> pathTokens) {
        return pathTokens==null?"":String.join("_", pathTokens);
    }

    public static <T> List<List<T>> splitListInBatches(List<T> list, final int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        if(list != null) {
            final int listSize = list.size();
            for (int i = 0; i < listSize; i += batchSize) {
                batches.add(new ArrayList<>(
                        list.subList(i, Math.min(listSize, i + batchSize)))
                );
            }
        }

        return batches;
    }
}
