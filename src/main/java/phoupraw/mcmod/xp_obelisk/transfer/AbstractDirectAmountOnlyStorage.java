package phoupraw.mcmod.xp_obelisk.transfer;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public abstract class AbstractDirectAmountOnlyStorage<T> extends SnapshotParticipant<Long> implements AmountOnlyStorage<T> {
    @Override
    protected Long createSnapshot() {
        return getAmount();
    }
    @Override
    protected void readSnapshot(Long snapshot) {
        setAmount(snapshot);
    }
    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        long amount = getAmount();
        long add = Math.min(maxAmount, getCapacity() - amount);
        if (add != 0) {
            updateSnapshots(transaction);
            setAmount(amount + add);
        }
        return add;
    }
    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        long amount = getAmount();
        long sub = Math.min(maxAmount, amount);
        if (sub != 0) {
            updateSnapshots(transaction);
            setAmount(amount - sub);
        }
        return sub;
    }
    public abstract void setAmount(long amount);
}
