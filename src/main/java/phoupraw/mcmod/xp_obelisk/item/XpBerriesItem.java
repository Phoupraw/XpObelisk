package phoupraw.mcmod.xp_obelisk.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class XpBerriesItem extends Item {
    public static final  int XP_PER_BERRIE = 3;
    public XpBerriesItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ServerWorld sw = world instanceof ServerWorld ? (ServerWorld)world : null;
        PlayerEntity pe = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (sw != null && pe != null) {
            //ExperienceOrbEntity.spawn(sw, user.getPos(), XpStorage.XP_PER_BERRIE);
            if (pe.isSneaking()) {
                world.spawnEntity(new ExperienceOrbEntity(world, user.getX(), user.getY(), user.getZ(), XP_PER_BERRIE));
            } else {
                pe.addExperience(XP_PER_BERRIE);
            }
        }

       return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(stack,world,tooltip,tooltipContext);
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.tooltip.xp_berrie").formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("item.tooltip.xp_berrie_sneak").formatted(Formatting.AQUA));
        } else {
            tooltip.add(Text.translatable("item.xps.more.info.tooltip"));
        }
    }

}
