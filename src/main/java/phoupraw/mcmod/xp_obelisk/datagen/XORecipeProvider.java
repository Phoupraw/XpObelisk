package phoupraw.mcmod.xp_obelisk.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

import java.util.function.Consumer;

public class XORecipeProvider extends FabricRecipeProvider {
    public XORecipeProvider(FabricDataOutput output) {
        super(output);
    }
    //@Override
    //public void generate(RecipeExporter exporter) {
        //AdvancementCriterion<TickCriterion.Conditions> tick = Criteria.TICK.create(new TickCriterion.Conditions(Optional.empty()));
        //new ShapedRecipeJsonBuilder(RecipeCategory.MISC, XOItems.SOLID_FUEL_GENERATOR, 1)
        //  .criterion("stupidMojang", tick)
        //  .input('A', Items.COPPER_INGOT)
        //  .input('B', Items.FURNACE)
        //  .pattern(" A ")
        //  .pattern("ABA")
        //  .pattern(" A ")
        //  .offerTo(exporter);
        //new ShapedRecipeJsonBuilder(RecipeCategory.TOOLS, XOItems.CARBON_BATTERY, 1)
        //  .criterion("stupidMojang", tick)
        //  .input('A', Items.CHARCOAL)
        //  .input('B', ItemTags.WOODEN_SLABS)
        //  .input('C', Items.LIGHTNING_ROD)
        //  .pattern("BCB")
        //  .pattern("BAB")
        //  .pattern("BAB")
        //  .offerTo(exporter);
        //new ShapedRecipeJsonBuilder(RecipeCategory.TOOLS, XOItems.BUCKET, 4)
        //  .criterion("stupidMojang", tick)
        //  .input('A', Items.CAULDRON)
        //  .input('B', Items.BUCKET)
        //  .input('C', Items.GLASS_PANE)
        //  .pattern(" A ")
        //  .pattern("CBC")
        //  .pattern(" C ")
        //  .offerTo(exporter);
    //}
    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {

    }
}
