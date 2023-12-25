package phoupraw.mcmod.xp_obelisk.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Unmodifiable;
import phoupraw.mcmod.xp_obelisk.consts.XOItems;

import java.util.List;

public class XpBerriesBushBlock extends CropBlock {
    public static final @Unmodifiable List<VoxelShape> AGE_TO_SHAPE = List.of(
      createCuboidShape(4, 0, 4, 12, 5, 12),
      createCuboidShape(4, 0, 4, 12, 7, 12),
      createCuboidShape(3, 0, 3, 13, 9, 13),
      createCuboidShape(2, 0, 2, 14, 12, 14),
      createCuboidShape(1, 0, 1, 15, 15, 15),
      createCuboidShape(1, 0, 1, 15, 16, 15),
      createCuboidShape(1, 0, 1, 15, 16, 15),
      createCuboidShape(1, 0, 1, 15, 16, 15)
    );
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }
    public XpBerriesBushBlock(Settings settings) {
        super(settings);
    }
    public ItemConvertible getSeedsItem() {
        return XOItems.XP_BERRIES_SEEDS;
    }
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE.get(state.get(this.getAgeProperty()));
    }
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.SOUL_SOIL);
    }
    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < MAX_AGE;
    }
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        //super.randomTick(state, world, pos, random);
        int i = state.get(AGE);
        if (i < MAX_AGE && random.nextInt(10) == 0) {
            state = state.with(AGE, i + 1);
            world.setBlockState(pos, state, 2);
        }
    }
    public boolean isFullGrown(BlockState state) {
        return state.get(AGE) == MAX_AGE;
    }
    public boolean isRipe(BlockState state) {
        return state.get(AGE) > MAX_AGE - 2;
    }
    @Override
    public void grow(ServerWorld world, net.minecraft.util.math.random.Random random, BlockPos pos, BlockState state) {
        int i = Math.min(MAX_AGE, state.get(AGE) + 1);
        world.setBlockState(pos, state.with(AGE, i), 2);
    }
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this.getSeedsItem());
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //dropStack(world, pos, new ItemStack(ModItems.XP_BERRIES_SEEDS, 1));
        super.onBreak(world, pos, state, player);
    }
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!isFullGrown(state) && player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS;
        } else if (isRipe(state)) {
            int bonusDrops = 2 + world.random.nextInt(4);
            dropStack(world, pos, new ItemStack(XOItems.XP_BERRIES, (isFullGrown(state) ? bonusDrops : world.random.nextInt(3))));
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlockState(pos, state.with(AGE, MAX_AGE - 3), 2);
            return ActionResult.success(world.isClient);
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (isFullGrown(state) && random.nextInt(20) == 0) {

            double targetX = pos.getX() + 0.5D;
            double targetY = pos.getY() + 1.1D;
            double targetZ = pos.getZ() + 0.5D;

            world.addParticle(ParticleTypes.SCRAPE, targetX, targetY, targetZ, 0.1, 1.3, 0.1);
        }
    }
}
