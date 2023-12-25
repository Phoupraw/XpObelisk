package phoupraw.mcmod.xp_obelisk.misc;

import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class ConstItemApiProvider<A,C> implements ItemApiLookup.ItemApiProvider<A,C> {
    public final A api;
    @Override
    public @NotNull A find(ItemStack itemStack, C context) {
        return api;
    }
}
