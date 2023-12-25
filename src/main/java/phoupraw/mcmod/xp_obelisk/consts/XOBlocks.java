package phoupraw.mcmod.xp_obelisk.consts;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import phoupraw.mcmod.xp_obelisk.block.XpBerriesBushBlock;
import phoupraw.mcmod.xp_obelisk.block.XpObeliskBlock;

@ApiStatus.NonExtendable
public interface XOBlocks {
    Block XP_OBELISK = r(XOIdentifiers.BLOCK_XP_OBELISK,new XpObeliskBlock(FabricBlockSettings
      .create()
      .sounds(BlockSoundGroup.METAL)
      .strength(0.25f, 1000.0f)
      .requiresTool()
      .luminance(10)));
    Block SOUL_COPPER_BLOCK = r(XOIdentifiers.BLOCK_SOUL_COPPER,new Block(FabricBlockSettings
      .create()
      .sounds(BlockSoundGroup.METAL)
      .strength(0.25f, 1000f)
      .requiresTool()
    ));
    CropBlock XP_BERRIES_BUSH = r(XOIdentifiers.XP_BERRIE_BUSH_BLOCK,new XpBerriesBushBlock(FabricBlockSettings.create()
      .nonOpaque()
      .noCollision()
      .ticksRandomly()
      .breakInstantly()
      .sounds(BlockSoundGroup.CROP)
      .emissiveLighting(Blocks::always)));
    FluidBlock LIQUID_XP = r(XOIdentifiers.XP_FLUID, new FluidBlock(XOFluids.LIQUID_XP_STILL, FabricBlockSettings.create()
      .liquid()
      .noCollision()
      .luminance(10)));
    //Block SOLID_FUEL_GENERATOR = r(TIIdentifiers.SOLID_FUEL_GENERATOR, new SolidFuelGeneratorBlock(machine().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER).luminance(SolidFuelGeneratorBlock::luminance)));
    //Block CARBON_BATTERY = r(TIIdentifiers.CARBON_BATTERY, new BatteryBlock(machine().mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.WOOD)));
    //Block REDSTONE_BATTERY = r(TIIdentifiers.REDSTONE_BATTERY, new BatteryBlock(machine().mapColor(MapColor.RED).sounds(BlockSoundGroup.STONE)));
    //Block ELEC_FURNACE = r(TIIdentifiers.ELEC_FURNACE, new ElectricFurnaceBlock(machine().mapColor(MapColor.IRON_GRAY).sounds(BlockSoundGroup.STONE)));
    //Block ELEC_BLAST = r(TIIdentifiers.ELEC_BLAST, new ElecBlastBlock(machine().mapColor(MapColor.IRON_GRAY).sounds(BlockSoundGroup.STONE)));
    //Block LOW_TRANSFORMER = r(TIIdentifiers.LOW_TRANSFORMER, new Block(machine().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER)));
    //Block BUCKET = r(TIIdentifiers.BUCKET, new BucketBlock(machine().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.STONE)./*nonOpaque().*/luminance(LightBlock.STATE_TO_LUMINANCE)));
    //private static FabricBlockSettings machine() {
    //    return FabricBlockSettings.create().requiresTool().strength(3, 6);
    //}
    @ApiStatus.Internal
    static void initCommon() {

    }
    @Contract("_, _ -> param2")
    private static <T extends Block> T r(Identifier id, T block) {
        return Registry.register(Registries.BLOCK, id, block);
    }
}
