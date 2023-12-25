package phoupraw.mcmod.xp_obelisk.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TickableBlockEntity {
    /**
     如果把{@code <T extends BlockEntity & TickableBlockEntity>}改为{@code <T extends TickableBlockEntity>}，编译不报错，但运行时崩溃
     */
    static <T extends BlockEntity & TickableBlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T blockEntity) {
        blockEntity.tick(world, blockPos, blockState);
    }
    void tick(World world, BlockPos blockPos, BlockState blockState);
}
