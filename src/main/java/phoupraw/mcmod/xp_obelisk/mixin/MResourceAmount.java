package phoupraw.mcmod.xp_obelisk.mixin;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ResourceAmount.class,remap = false)
abstract class MResourceAmount<T> implements StorageView<T> {
    @Shadow
    public abstract T resource();
    @Shadow
    public abstract long amount();
    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }
    @Override
    public boolean isResourceBlank() {
        return getAmount() == 0 || getResource() instanceof TransferVariant<?> variant && variant.isBlank();
    }
    @Override
    public T getResource() {
        return resource();
    }
    @Override
    public long getAmount() {
        return amount();
    }
    @Override
    public long getCapacity() {
        return getAmount();
    }
}
