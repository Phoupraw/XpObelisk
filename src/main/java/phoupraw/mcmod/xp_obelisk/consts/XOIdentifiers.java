package phoupraw.mcmod.xp_obelisk.consts;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.xp_obelisk.XpObelisk;

@ApiStatus.NonExtendable
public interface XOIdentifiers {
    Identifier SOLID_FUEL_GENERATOR = of("solid_fuel_generator");
    Identifier TRANSFORMER = of("transformer");
    Identifier LOW_TRANSFORMER = TRANSFORMER.withPrefixedPath("low_");
    Identifier WRENCH = of("wrench");
    Identifier DRILL = of("drill");
    Identifier BATTERY = of("battery");
    Identifier REDSTONE_BATTERY = BATTERY.withPrefixedPath("redstone_");
    Identifier CARBON_BATTERY = BATTERY.withPrefixedPath("carbon_");
    Identifier ELEC = of("elec");
    Identifier ELEC_FURNACE = ELEC.withSuffixedPath("_furnace");
    Identifier ELEC_BLAST = ELEC.withSuffixedPath("_blast");
    Identifier EXPERIENCE = of("experience");
    Identifier BUCKET = of("bucket");
    Identifier ELEC_MENDING = ELEC.withSuffixedPath("_mending");
    Identifier XP_FLUID = of("xp_fluid"),
      XP_BUCKET = of("xp_bucket"),
      FLOWING_XP = of("flowing_xp"),
      BLOCK_XP_OBELISK = of("block_xp_obelisk"),
      BLOCK_SOUL_COPPER = of("block_soul_copper"),
      INSPECTOR = of("inspector"),
      KEY = of("key"),
      LOCK = of("lock"),
      XP_BERRIE_BUSH_BLOCK = of("xp_berrie_bush_block"),
      XP_BERRIES_SEEDS = of("xp_berries_seeds"),
      XP_BERRIES = of("xp_berries"),
      XP_REMOVER = of("xp_remover"),
      XP_DUST = of("xp_dust");
    Identifier XPS_LEXICA = new Identifier(XpObelisk.ID, "xps_lexica"),
      HANDBOOK = of("handbook"),
      XP_ROD = of("xp_rod"),
      SOUL_COPPER_BLEND = of("soul_copper_blend"),
      SOUL_COPPER_INGOT = of("soul_copper_ingot"),
      SOUL_COPPER_NUGGET = of("soul_copper_nugget");
    @ApiStatus.Internal
    static Identifier of(String path) {
        return new Identifier(XpObelisk.ID, path);
    }
}
