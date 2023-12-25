package phoupraw.mcmod.xp_obelisk.transfer.xp;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import phoupraw.mcmod.xp_obelisk.transfer.ItemSingleVariantStorage;

public class FixedItemExpStorage extends ItemSingleVariantStorage<ExpSingleton> implements ExtractionOnlyStorage<ExpSingleton>,ExpStorage {
    public final long amount;
    public final ItemVariant emptied;
    public FixedItemExpStorage(ContainerItemContext context, long amount, ItemVariant emptied) {
        super(context);
        this.amount = amount;
        this.emptied = emptied;
    }
    @Override
    protected ExpSingleton getBlankResource() {
        return ExpSingleton.XP;
    }
    @Override
    protected ExpSingleton getResource(ItemVariant currentVariant) {
        return ExpSingleton.XP;
    }
    @Override
    protected long getAmount(ItemVariant currentVariant) {
        return amount;
    }
    @Override
    protected long getCapacity(ExpSingleton variant) {
        return amount;
    }
    @Override
    protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, ExpSingleton newResource, long newAmount) {
        return newAmount == 0 ? emptied : currentVariant;
    }
    @Override
    public long insert(ExpSingleton insertedResource, long maxAmount, TransactionContext transaction) {
        return 0;
    }
    @Override
    public boolean isResourceBlank() {
        return getAmount()==0;
    }
    @Override
    public boolean supportsInsertion() {
        return false;
    }
    @Override
    public long extract(ExpSingleton extractedResource, long maxAmount, TransactionContext transaction) {
        if (maxAmount<amount)return 0;
        return super.extract(extractedResource, amount , transaction);
    }
}
