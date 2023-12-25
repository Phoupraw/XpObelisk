package phoupraw.mcmod.xp_obelisk.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class XpRemoverItem extends Item {
    public XpRemoverItem(Settings settings) {
        super(settings);
    }
    public final static String MODE = "Mode";
    @Override
    public boolean hasGlint(ItemStack stack) {
        if (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).contains(MODE)) {
            return stack.getNbt().getBoolean(MODE);
        }
        return false;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = (user.getMainHandStack().getItem() instanceof XpRemoverItem) ? user.getMainHandStack() : user.getOffHandStack();

        NbtCompound nbt = getNbtCompound(stack);

        // Play sound when switching
        if (user.isSneaking()) {
            if (world.isClient) {
                user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }
            nbt.putBoolean(MODE, !nbt.getBoolean(MODE));
        }

        if (nbt.getBoolean(MODE)) {
            stack.setNbt(nbt);
        } else {
            stack.removeSubNbt(MODE);
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }
    private NbtCompound getNbtCompound(ItemStack stack) {
        NbtCompound nbt;
        if (stack.hasNbt()) {
            nbt = stack.getNbt();
        } else {
            nbt = new NbtCompound();
        }

        if (nbt != null && !nbt.contains(MODE)) {
            nbt.putBoolean(MODE, false);
        }

        return nbt;
    }
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(stack, world, tooltip, tooltipContext);
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.tooltip.xp_remover_sneak").formatted(Formatting.RED));
            NbtCompound nbt = getNbtCompound(stack);
            if (nbt.getBoolean(MODE)) {
                tooltip.add(Text.translatable("item.tooltip.xp_rod").formatted(Formatting.AQUA));
            } else {
                tooltip.add(Text.translatable("item.tooltip.xp_remover").formatted(Formatting.AQUA));
            }
            tooltip.add(Text.translatable("item.tooltip.stack_size"));
        } else {
            tooltip.add(Text.translatable("item.xps.more.info.tooltip"));
        }
    }
}
