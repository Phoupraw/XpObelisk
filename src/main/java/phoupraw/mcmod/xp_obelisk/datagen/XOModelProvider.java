package phoupraw.mcmod.xp_obelisk.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class XOModelProvider extends FabricModelProvider {
    public XOModelProvider(FabricDataOutput output) {
        super(output);
    }
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator g) {
        //for (Block block : Arrays.asList(XOBlocks.SOLID_FUEL_GENERATOR, XOBlocks.LOW_TRANSFORMER, XOBlocks.CARBON_BATTERY, XOBlocks.BUCKET)) {
        //    g.registerSimpleState(block);
        //}
        //g.excludeFromSimpleItemModelGeneration(XOBlocks.CARBON_BATTERY);
        //g.registerItemModel(XOItems.WRENCH);
        //g.registerItemModel(XOItems.REDSTONE_BATTERY);
    }
    @Override
    public void generateItemModels(ItemModelGenerator g) {

    }
}
