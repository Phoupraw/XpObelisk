package phoupraw.mcmod.xp_obelisk.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class XOBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public XOBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        //getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
        //  .add(XOBlocks.CARBON_BATTERY);
        //getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
        //  .add(XOBlocks.SOLID_FUEL_GENERATOR, XOBlocks.LOW_TRANSFORMER, XOBlocks.REDSTONE_BATTERY, XOBlocks.BUCKET, XOBlocks.ELEC_FURNACE);
        //getOrCreateTagBuilder(TIBlockTags.MINEABLE_WRENCH)
        //  .add(XOBlocks.SOLID_FUEL_GENERATOR, XOBlocks.LOW_TRANSFORMER, XOBlocks.CARBON_BATTERY, XOBlocks.REDSTONE_BATTERY, XOBlocks.BUCKET, XOBlocks.ELEC_FURNACE);
    }
}
