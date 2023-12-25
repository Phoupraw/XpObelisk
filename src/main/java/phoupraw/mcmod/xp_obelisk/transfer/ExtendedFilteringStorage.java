package phoupraw.mcmod.xp_obelisk.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class ExtendedFilteringStorage<T, S extends Storage<T>> extends FilteringStorage<T> {
    public ExtendedFilteringStorage(S backingStorage) {
        super(backingStorage);
    }
    @Override
    public boolean supportsInsertion() {
        return super.supportsInsertion();
    }
    @Override
    public boolean supportsExtraction() {
        return super.supportsExtraction();
    }
    @Override
    public long insert(T insertedResource, long maxAmount, TransactionContext transaction) {
        try (var t = transaction.openNested()) {
            long insertedAmount = super.insert(insertedResource, limitInsertion(insertedResource, maxAmount), t);
            if (requireInsertion(insertedResource, maxAmount, insertedAmount)) {
                t.commit();
                return insertedAmount;
            }
        }
        return 0;
    }
    @Override
    public long extract(T extractedResource, long maxAmount, TransactionContext transaction) {
        try (var t = transaction.openNested()) {
            long extractedAmount = super.extract(extractedResource, limitExtraction(extractedResource, maxAmount), t);
            if (requireExtraction(extractedResource, maxAmount, extractedAmount)) {
                t.commit();
                return extractedAmount;
            }
        }
        return 0;
    }
    /**
     如果永远返回{@code 0}，则应重写{@link #supportsInsertion()}
     */
    public long limitInsertion(T insertedResource, long maxAmount) {
        return maxAmount;
    }
    /**
     如果永远返回{@code 0}，则应重写{@link #supportsExtraction()}
     */
    public long limitExtraction(T extractedResource, long maxAmount) {
        return maxAmount;
    }
    public boolean requireInsertion(T insertedResource, long maxAmount, long insertedAmount) {
        return true;
    }
    public boolean requireExtraction(T extractedResource, long maxAmount, long extractedAmount) {
        return true;
    }
    @SuppressWarnings("unchecked")
    public S getBackingStorage() {
        return (S) backingStorage.get();
    }
}
