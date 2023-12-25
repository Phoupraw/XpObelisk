package phoupraw.mcmod.xp_obelisk.misc;

import net.minecraft.block.Block;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BlockUsage  extends BiFunction<Block, ItemUsageContext,@NotNull ActionResult> {
    @Override
    ActionResult apply(Block block, ItemUsageContext usageContext);
}
