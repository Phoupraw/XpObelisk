package phoupraw.mcmod.xp_obelisk.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEntityUpdateS2CPacket.class)
class MBlockEntityUpdateS2CPacket {
    /**
     为什么空NBT就直接为null？傻逼mojang，害我调试好久
     */
    @Redirect(method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/nbt/NbtCompound;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;isEmpty()Z"))
    private boolean isEmpty(NbtCompound instance) {
        return false;
    }
}
