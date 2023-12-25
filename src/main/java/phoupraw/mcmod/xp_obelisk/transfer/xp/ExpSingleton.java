package phoupraw.mcmod.xp_obelisk.transfer.xp;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
//import phoupraw.mcmod.xp_obelisk.transfer.energy.EnergySingleton;

public final class ExpSingleton implements TransferVariant<Void> {
    public static final ExpSingleton XP = new ExpSingleton();
    private ExpSingleton() {}
    @Override
    public boolean isBlank() {
        return false;
    }
    @Override
    public Void getObject() {
        return null;
    }
    @Override
    public @Nullable NbtCompound getNbt() {
        return null;
    }
    @Override
    public boolean hasNbt() {
        return false;
    }
    @Override
    public boolean nbtMatches(@Nullable NbtCompound other) {
        return true;
    }
    @Override
    public boolean isOf(Void object) {
        return true;
    }
    @Override
    public @Nullable NbtCompound copyNbt() {
        return null;
    }
    @Override
    public NbtCompound copyOrCreateNbt() {
        return new NbtCompound();
    }
    @Override
    public NbtCompound toNbt() {
        return new NbtCompound();
    }
    @Override
    public void toPacket(PacketByteBuf buf) {

    }
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
