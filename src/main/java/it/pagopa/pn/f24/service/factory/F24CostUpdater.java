package it.pagopa.pn.f24.service.factory;

import reactor.core.publisher.Mono;

public abstract class F24CostUpdater {
    /*
    protected F24Item f24Item;

    protected final String iun;
    protected final String recipientIndex;
    protected final PnDeliveryPushClientImpl pnDeliveryPushClient;

    public F24CostUpdater(F24Item f24Item, String iun, String recipientIndex, PnDeliveryPushClientImpl pnDeliveryPushClient) {
        this.f24Item = f24Item;
        this.iun = iun;
        this.recipientIndex = recipientIndex;
        this.pnDeliveryPushClient = pnDeliveryPushClient;
    }

    public Mono<F24Item> updateCost() {
        if(shouldIncludeNotificationCost()) {
            return pnDeliveryPushClient.notificationProcessCost(iun, recipientIndex)
                    .map(res -> incrementCost(res.getAmount()));
        }

        return Mono.just(f24Item);
    }

    protected abstract F24Item incrementCost(Integer amount);

    protected abstract boolean shouldIncludeNotificationCost();

    protected String sumDebitAndAmount(String debit, Integer amount) {
        return String.valueOf(Integer.parseInt(debit) + amount);
    }
     */
}
