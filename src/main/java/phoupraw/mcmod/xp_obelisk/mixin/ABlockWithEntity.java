package phoupraw.mcmod.xp_obelisk.mixin;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockWithEntity.class)
public interface ABlockWithEntity {
    @Invoker
    static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> invokeCheckType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return null;
    }
}
