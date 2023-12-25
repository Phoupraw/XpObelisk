package phoupraw.mcmod.xp_obelisk.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.xp_obelisk.block.entity.NameableBlockEntity;

public class NameableBlock extends Block {
    public static void onPlaced(Block self, World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof NameableBlockEntity nameable && !nameable.hasCustomName()) {
            nameable.setCustomName(itemStack.getName());
        }
    }
    public NameableBlock(Settings settings) {
        super(settings);
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        onPlaced(this, world, pos, state, placer, itemStack);
    }
}
