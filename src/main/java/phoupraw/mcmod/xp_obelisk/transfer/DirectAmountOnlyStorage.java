package phoupraw.mcmod.xp_obelisk.transfer;

import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import org.jetbrains.annotations.Nullable;

public abstract class DirectAmountOnlyStorage<T> extends AbstractDirectAmountOnlyStorage<T> {
    protected long amount;
    public @Nullable NbtElement toNbt() {
        return isResourceBlank() ? null : NbtLong.of(getAmount());
    }
    public void readNbt(@Nullable NbtElement nbt) {
        setAmount(nbt instanceof AbstractNbtNumber nbtNumber ? nbtNumber.longValue() : 0);
    }
    @Override
    public long getAmount() {
        return this.amount;
    }
    @Override
    public void setAmount(long amount) {
        this.amount = amount;
    }
}
