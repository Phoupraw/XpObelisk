package phoupraw.mcmod.xp_obelisk.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HasGlinttem extends Item {
    public HasGlinttem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
