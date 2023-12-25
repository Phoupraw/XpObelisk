package phoupraw.mcmod.xp_obelisk.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public interface AmountOnlyStorage<T> extends SingleSlotStorage<T> {
    @Override
    default boolean isResourceBlank() {
        return getAmount() == 0;
    }
}
