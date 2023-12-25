package phoupraw.mcmod.xp_obelisk.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.xp_obelisk.block.entity.TickableBlockEntity;
import phoupraw.mcmod.xp_obelisk.mixin.ABlockWithEntity;

public abstract class BlockEntityBlock extends NameableBlock implements BlockEntityProvider {
    public static <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Block self, World world, BlockState state, BlockEntityType<T> type, BlockEntityType<? extends TickableBlockEntity> expectedType) {
        return/* world.isClient() ? null :*/ ABlockWithEntity.invokeCheckType(type, expectedType, TickableBlockEntity::tick);
    }
    public static <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTickerServer(Block self, World world, BlockState state, BlockEntityType<T> type, BlockEntityType<? extends TickableBlockEntity> expectedType) {
        return world.isClient() ? null : getTicker(self, world, state, type, expectedType);
    }
    //public static <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(BlockEntityBlock<? extends TickableBlockEntity> self, World world, BlockState state, BlockEntityType<T> type) {
    //    return ABlockWithEntity.invokeValidateTicker(type, self.getBlockEntityType(), TickableBlockEntity::tick);
    //}
    public BlockEntityBlock(Settings settings) {
        super(settings);
    }
    @SuppressWarnings("deprecation")
    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return super.onSyncedBlockEvent(state, world, pos, type, data) | (blockEntity != null && blockEntity.onSyncedBlockEvent(type, data));
    }
    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BlockEntityProvider.super.getTicker(world, state, type);
    }
    @Override
    public abstract @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state);
    //public abstract BlockEntityType<? extends E> getBlockEntityType();
}
