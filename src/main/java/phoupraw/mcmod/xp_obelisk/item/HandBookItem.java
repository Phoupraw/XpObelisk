package phoupraw.mcmod.xp_obelisk.item;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import phoupraw.mcmod.xp_obelisk.consts.XOIdentifiers;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

public class HandBookItem extends Item {
    public static boolean isPatchouliLoaded() {
        return FabricLoader.getInstance().isModLoaded("patchouli");
    }
    public HandBookItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        if (!isPatchouliLoaded()) {
            return TypedActionResult.fail(stackInHand);
        }
        if (user instanceof ServerPlayerEntity player) {
            PatchouliAPI.get().openBookGUI(player, XOIdentifiers.XPS_LEXICA);
        }
        return TypedActionResult.success(stackInHand);
    }
    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        //tooltip.add(Text.of("WIP"));
        if (!isPatchouliLoaded())
            tooltip.add(Text.translatable("item.xps.patchouli_book.tooltip"));
    }
}

