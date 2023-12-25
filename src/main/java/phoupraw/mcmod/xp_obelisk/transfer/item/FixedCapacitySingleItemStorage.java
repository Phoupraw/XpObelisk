package phoupraw.mcmod.xp_obelisk.transfer.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
@Getter
@RequiredArgsConstructor
public class FixedCapacitySingleItemStorage extends SingleItemStorage {
    public final long capacity;
    @Override
    protected long getCapacity(ItemVariant variant) {
        return getCapacity();
    }
}
