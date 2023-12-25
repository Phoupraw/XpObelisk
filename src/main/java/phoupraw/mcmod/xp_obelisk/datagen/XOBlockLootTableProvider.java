package phoupraw.mcmod.xp_obelisk.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class XOBlockLootTableProvider extends FabricBlockLootTableProvider {
    @Contract("_, _ -> param1")
    public static CopyNbtLootFunction.Builder copyNbt(CopyNbtLootFunction.@NotNull Builder builder, String nbtPath) {
        return builder.withOperation(nbtPath, "BlockEntityTag." + nbtPath);
    }
    @Contract("_, _ -> param1")
    public static CopyNbtLootFunction.Builder copyNbt(CopyNbtLootFunction.Builder builder, String... nbtPaths) {
        for (String nbtPath : nbtPaths) {
            copyNbt(builder, nbtPath);
        }
        return builder;
    }
    public XOBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }
    @Override
    public void generate() {
        //addBlockEntityTag(XOBlocks.SOLID_FUEL_GENERATOR, "energyS", "chargeS", "fuelS", "machineS", "fuelTotal", "fuelLeft");
        //for (Block block : new Block[]{XOBlocks.CARBON_BATTERY, XOBlocks.REDSTONE_BATTERY}) {
        //    addBlockEntityTag(block, BatteryBlockEntity.NBT_KEY, "dischargeS", "chargeS", "transS", "output", "outputSides");
        //}
        //addBlockEntityTag(XOBlocks.ELEC_FURNACE, "progress");
        //addBlockEntityTag(XOBlocks.BUCKET, "storage");
    }
    private void addBlockEntityTag(Block block, String... nbtPaths) {
        addDrop(block, LootTable.builder()
          .pool(LootPool.builder()
            .with(ItemEntry.builder(block)
              .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
              .apply(copyNbt(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY), nbtPaths)))));
    }
}
