package phoupraw.mcmod.xp_obelisk.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class XOItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public XOItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture, new XOBlockTagProvider(output, completableFuture));
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        //getOrCreateTagBuilder(ItemTags.TOOLS).add(XOItems.WRENCH);
        //getOrCreateTagBuilder(TIItemTags.TRANSFORMER).add(XOItems.LOW_TRANSFORMER);
    }
}
