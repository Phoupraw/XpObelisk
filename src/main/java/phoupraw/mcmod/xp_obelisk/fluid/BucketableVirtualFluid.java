package phoupraw.mcmod.xp_obelisk.fluid;

import lombok.Getter;
import net.minecraft.item.Item;

public class BucketableVirtualFluid extends VirtualFluid {
    @Getter
    protected final Item bucketItem;
    public BucketableVirtualFluid(Item bucketItem) {
        this.bucketItem = bucketItem;
    }
}
