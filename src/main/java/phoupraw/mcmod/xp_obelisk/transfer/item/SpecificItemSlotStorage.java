package phoupraw.mcmod.xp_obelisk.transfer.item;

import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;

@RequiredArgsConstructor
public class SpecificItemSlotStorage extends MaxCountSlotStorage {
    public final Item item;
    @Override
    protected boolean canInsert(ItemVariant variant) {
        return super.canInsert(variant) && variant.isOf(item);
    }
}
