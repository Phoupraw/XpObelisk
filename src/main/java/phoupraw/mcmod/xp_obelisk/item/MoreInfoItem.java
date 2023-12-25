package phoupraw.mcmod.xp_obelisk.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public  class MoreInfoItem extends Item {
    public MoreInfoItem(Settings settings) {super(settings);}
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.tooltip." + Registries.ITEM.getId(this).getPath()).formatted(Formatting.AQUA));
        } else {
            tooltip.add(Text.translatable("item.xps.more.info.tooltip"));
        }
    }
}
