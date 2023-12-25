package phoupraw.mcmod.xp_obelisk.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

public class UnstackableSlotStorage extends DirectSingleItemStorage {
    @Override
    protected long getCapacity(ItemVariant variant) {
        return 1;
    }
}
