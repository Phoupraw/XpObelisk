package phoupraw.mcmod.xp_obelisk.consts;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.xp_obelisk.block.entity.XpObeliskBlockEntity;

@ApiStatus.NonExtendable
public interface XOBlockEntityTypes {
    BlockEntityType<XpObeliskBlockEntity> XP_OBELISK = of(XOIdentifiers.BLOCK_XP_OBELISK, XpObeliskBlockEntity::new,XOBlocks.XP_OBELISK);
    @ApiStatus.Internal
    static void initCommon() {

    }
    //BlockEntityType<SolidFuelGeneratorBlockEntity> SOILD_FUEL_GENERATOR = of(TIIdentifiers.SOLID_FUEL_GENERATOR, SolidFuelGeneratorBlockEntity::of, XOBlocks.SOLID_FUEL_GENERATOR);
    //BlockEntityType<ElectricFurnaceBlockEntity> ELEC_FURNACE = of(TIIdentifiers.ELEC_FURNACE, ElectricFurnaceBlockEntity::of, XOBlocks.ELEC_FURNACE);
    //BlockEntityType<ElecBlastBlockEntity> ELEC_BLAST = of(TIIdentifiers.ELEC_BLAST, ElecBlastBlockEntity::new, XOBlocks.ELEC_BLAST);
    //BlockEntityType<BatteryBlockEntity> BATTERY = of(TIIdentifiers.BATTERY, BatteryBlockEntity::new, XOBlocks.CARBON_BATTERY, XOBlocks.REDSTONE_BATTERY);
    ////BlockEntityType<RedstoneBatteryBlockEntity> REDSTONE_BATTERY = of(TIIdentifiers.REDSTONE_BATTERY, RedstoneBatteryBlockEntity::new, TIBlocks.REDSTONE_BATTERY);
    //BlockEntityType<BucketBlockEntity> BUCKET = of(TIIdentifiers.BUCKET, BucketBlockEntity::of, XOBlocks.BUCKET);
    private static <T extends BlockEntity> BlockEntityType<T> of(Identifier id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }
}
