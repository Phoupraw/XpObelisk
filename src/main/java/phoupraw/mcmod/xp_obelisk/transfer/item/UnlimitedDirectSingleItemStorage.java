package phoupraw.mcmod.xp_obelisk.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

public class UnlimitedDirectSingleItemStorage extends DirectSingleItemStorage {
    @Override
    protected long getCapacity(ItemVariant variant) {
        return Long.MAX_VALUE;
    }
}
