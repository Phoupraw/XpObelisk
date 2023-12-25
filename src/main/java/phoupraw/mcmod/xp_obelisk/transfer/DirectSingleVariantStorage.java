/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phoupraw.mcmod.xp_obelisk.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.xp_obelisk.misc.XOUtils;
import phoupraw.mcmod.xp_obelisk.transfer.item.DirectSingleItemStorage;

/**
 A storage that can store a single transfer variant at any given time.
 Implementors should at least override {@link #getCapacity(TransferVariant)},
 and probably {@link #onFinalCommit} as well for {@code markDirty()} and similar calls.

 <p>{@link #canInsert} and {@link #canExtract} can be used for more precise control over which variants may be inserted or extracted.
 If one of these two functions is overridden to always return false, implementors may also wish to override
 {@link #supportsInsertion} and/or {@link #supportsExtraction}.

 <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 @see DirectSingleItemStorage DirectSingleItemStorage for item variants. */
@ApiStatus.Experimental
public abstract class DirectSingleVariantStorage<T extends TransferVariant<?>> extends SnapshotParticipant<ResourceAmount<T>> implements SingleSlotStorage<T> {
    protected T resource = getBlankVariant();
    protected long amount = 0;
    public void setResource(T resource) {
        this.resource = resource;
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }
    /**
     Return the blank variant.

     <p>Note: this is called very early in the constructor.
     If fields need to be accessed from this function, make sure to re-initialize {@link #resource} yourself.
     */
    protected abstract T getBlankVariant();
    /**
     Return the maximum capacity of this storage for the passed transfer variant.
     If the passed variant is blank, an estimate should be returned.
     */
    protected abstract long getCapacity(T variant);
    /**
     @return {@code true} if the passed non-blank variant can be inserted, {@code false} otherwise.
     */
    protected boolean canInsert(T variant) {
        return true;
    }
    /**
     @return {@code true} if the passed non-blank variant can be extracted, {@code false} otherwise.
     */
    protected boolean canExtract(T variant) {
        return true;
    }
    /**
     Simple implementation of writing to NBT. Other formats are allowed, this is just a convenient suggestion.
     */
    // Reading from NBT is not provided because it would need to call the static FluidVariant/ItemVariant.fromNbt
    public @Nullable NbtCompound writeNbt(@NotNull NbtCompound root) {
        return XOUtils.clearDefault(root.copyFrom(XOUtils.toNbt(getResource(),getAmount())));
    }
    @Override
    public long insert(T insertedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

        if ((insertedVariant.equals(getResource()) || getResource().isBlank()) && canInsert(insertedVariant)) {
            long insertedAmount = Math.min(maxAmount, getCapacity(insertedVariant) - getAmount());

            if (insertedAmount > 0) {
                updateSnapshots(transaction);

                if (getResource().isBlank()) {
                    setResource(insertedVariant);
                    setAmount(insertedAmount);
                } else {
                    setAmount(getAmount() + insertedAmount);
                }

                return insertedAmount;
            }
        }

        return 0;
    }
    @Override
    public long extract(T extractedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(extractedVariant, maxAmount);

        if (extractedVariant.equals(getResource()) && canExtract(extractedVariant)) {
            long extractedAmount = Math.min(maxAmount, getAmount());

            if (extractedAmount > 0) {
                updateSnapshots(transaction);
                setAmount(getAmount() - extractedAmount);

                if (getAmount() == 0) {
                    setResource(getBlankVariant());
                }

                return extractedAmount;
            }
        }

        return 0;
    }
    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }
    @Override
    public T getResource() {
        return resource;
    }
    @Override
    public long getAmount() {
        return amount;
    }
    @Override
    public long getCapacity() {
        return getCapacity(getResource());
    }
    @Override
    protected ResourceAmount<T> createSnapshot() {
        return new ResourceAmount<>(getResource(), getAmount());
    }
    @Override
    protected void readSnapshot(ResourceAmount<T> snapshot) {
        setResource(snapshot.resource());
        setAmount(snapshot.amount());
    }
    @Override
    public String toString() {
        return "SingleVariantStorage[%d %s]".formatted(getAmount(), getResource());
    }
}
