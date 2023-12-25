package phoupraw.mcmod.xp_obelisk.transfer.xp;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import phoupraw.mcmod.xp_obelisk.transfer.ItemSingleVariantStorage;

import java.util.Iterator;

import static phoupraw.mcmod.xp_obelisk.transfer.xp.ExpSingleton.XP;

public  class MendingExpStorage extends ItemSingleVariantStorage<ExpSingleton> implements InsertionOnlyStorage<ExpSingleton> {
    public MendingExpStorage(ContainerItemContext context) {
        super(context);
    }
    @Override
    protected ExpSingleton getBlankResource() {
        return XP;
    }
    @Override
    protected ExpSingleton getResource(ItemVariant currentVariant) {
        return XP;
    }
    @Override
    protected long getAmount(ItemVariant currentVariant) {
        Item item = currentVariant.getItem();
        return (item.getMaxDamage() - currentVariant.toStack().getDamage())/2;
    }
    @Override
    protected long getCapacity(ExpSingleton variant) {
        return getItem().getMaxDamage() / 2;
    }
    @Override
    protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, ExpSingleton newResource, long newAmount) {
        ItemStack stack = currentVariant.toStack();
        stack.setDamage((int) (newAmount * 2));
        return ItemVariant.of(stack);
    }
    @Override
    public boolean isResourceBlank() {
        return getAmount() == 0;
    }
    @Override
    public @NotNull Iterator<StorageView<ExpSingleton>> iterator() {
        return super.iterator();
    }
    @Override
    public long extract(ExpSingleton extractedResource, long maxAmount, TransactionContext transaction) {
        return 0;
    }
    @Override
    public boolean supportsExtraction() {
        return false;
    }
}
