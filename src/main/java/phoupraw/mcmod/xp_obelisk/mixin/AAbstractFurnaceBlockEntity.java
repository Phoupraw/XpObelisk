package phoupraw.mcmod.xp_obelisk.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AAbstractFurnaceBlockEntity {
    @Accessor
    Object2IntOpenHashMap<Identifier> getRecipesUsed();
}
