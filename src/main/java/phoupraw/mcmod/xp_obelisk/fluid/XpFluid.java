package phoupraw.mcmod.xp_obelisk.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.xp_obelisk.consts.XOBlocks;
import phoupraw.mcmod.xp_obelisk.consts.XOFluids;
import phoupraw.mcmod.xp_obelisk.consts.XOItems;

public abstract sealed class XpFluid extends FlowableFluid {
    public static final long MB_PER_XP = FluidConstants.BUCKET / 100;
    @ApiStatus.Internal
    public static void initCommon() {
        FluidStorage.combinedItemApiProvider(Items.EXPERIENCE_BOTTLE).register(context -> new FullItemFluidStorage(context, Items.GLASS_BOTTLE, FluidVariant.of(XOFluids.LIQUID_XP_STILL), FluidConstants.BOTTLE));
        FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(context -> new EmptyItemFluidStorage(context, Items.EXPERIENCE_BOTTLE, XOFluids.LIQUID_XP_STILL, FluidConstants.BOTTLE));
    }
    /**
     @return whether the given fluid an instance of this fluid
     */
    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }
    @Override
    protected boolean isInfinite(World world) {
        return false;
    }
    /**
     Perform actions when fluid flows into a replaceable block. Water drops
     the block's loot table. Lava plays the "block.lava.extinguish" sound.
     */
    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }
    @Override
    public Item getBucketItem() {
        return XOItems.BUCKETED_LIQUID_XP;
    }
    /**
     Lava returns true if its FluidState is above a certain height and the
     Fluid is Water.
     @return whether the given Fluid can flow into this FluidState
     */
    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }
    /**
     Possibly related to the distance checks for flowing into nearby holes?
     Water returns 4. Lava returns 2 in the Overworld and 4 in the Nether.
     */
    @Override
    protected int getFlowSpeed(WorldView worldView) {
        return 3;
    }
    /**
     Water returns 1. Lava returns 2 in the Overworld and 1 in the Nether.
     */
    @Override
    protected int getLevelDecreasePerBlock(WorldView worldView) {
        return 2;
    }
    /**
     Water returns 5. Lava returns 30 in the Overworld and 10 in the Nether.
     */
    @Override
    public int getTickRate(WorldView worldView) {
        return 15;
    }
    /**
     Water and Lava both return 100.0F.
     */
    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }
    @Override
    protected BlockState toBlockState(FluidState state) {
        return XOBlocks.LIQUID_XP.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }
    public static final class Still extends XpFluid {
        @Override
        public Fluid getFlowing() {
            return XOFluids.LIQUID_XP_FLOWING;
        }
        @Override
        public Fluid getStill() {
            return this;
        }
        @Override
        public boolean isStill(FluidState state) {
            return state.isOf(this);
        }
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
    }
    public static final class Flowing extends XpFluid {
        @Override
        public Fluid getFlowing() {
            return this;
        }
        @Override
        public Fluid getStill() {
            return XOFluids.LIQUID_XP_STILL;
        }
        @Override
        public boolean isStill(FluidState state) {
            return !state.isOf(this);
        }
        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }
    }
}
