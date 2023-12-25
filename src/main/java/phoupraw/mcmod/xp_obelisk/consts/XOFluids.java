package phoupraw.mcmod.xp_obelisk.consts;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.xp_obelisk.fluid.XpFluid;

public interface XOFluids {
    FlowableFluid LIQUID_XP_STILL = r(XOIdentifiers.XP_FLUID, new XpFluid.Still());
    FlowableFluid LIQUID_XP_FLOWING = r(XOIdentifiers.FLOWING_XP, new XpFluid.Flowing());
    @ApiStatus.Internal
    static void initCommon() {

    }
    private static <T extends Fluid> T r(Identifier id, T fluid) {
        return Registry.register(Registries.FLUID, id, fluid);
    }
}
