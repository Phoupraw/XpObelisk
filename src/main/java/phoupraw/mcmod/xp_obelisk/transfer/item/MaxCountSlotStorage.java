package phoupraw.mcmod.xp_obelisk.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

public class MaxCountSlotStorage extends DirectSingleItemStorage {
    @Override
    protected long getCapacity(ItemVariant variant) {
        return variant.toStack().getMaxCount();
    }
}
