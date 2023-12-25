package phoupraw.mcmod.xp_obelisk;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.xp_obelisk.block.XpObeliskBlock;
import phoupraw.mcmod.xp_obelisk.block.entity.render.StorageBlockEntityRenderer;
import phoupraw.mcmod.xp_obelisk.consts.XOBlockEntityTypes;
import phoupraw.mcmod.xp_obelisk.consts.XOBlocks;
import phoupraw.mcmod.xp_obelisk.consts.XOFluids;
import phoupraw.mcmod.xp_obelisk.consts.XOItems;
import phoupraw.mcmod.xp_obelisk.datagen.*;
import phoupraw.mcmod.xp_obelisk.fluid.XpFluid;
import phoupraw.mcmod.xp_obelisk.transfer.xp.ExpStorage;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public final class XpObelisk implements ModInitializer, ClientModInitializer, DataGeneratorEntrypoint {
    public static final String ID = "xps";
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger();
    @Override
    public void onInitialize() {
        XOBlocks.initCommon();
        XOItems.initCommon();
        XOFluids.initCommon();
        XOBlockEntityTypes.initCommon();
        //TIPacketTypes.MENU_SYNC.hashCode();
        //BatteryAttr.put(XOBlocks.CARBON_BATTERY, BatteryAttr.eu(1 << 13, 1 << 5));
        //BatteryAttr.put(XOBlocks.REDSTONE_BATTERY, BatteryAttr.eu(1 << 17, 1 << 7));
        //BatteryAttr.put(XOItems.DRILL, BatteryAttr.eu(1024 * 16, 32, 0));
        //TIUtils.put(TransformerBlock.PROPERTIES, XOBlocks.LOW_TRANSFORMER, 128 * EU);
        //ServerPlayNetworking.registerGlobalReceiver(TIPacketTypes.MENU_SYNC, MenuSyncPacket::receive);
        //ServerTickEvents.END_SERVER_TICK.register(BatteryItem::charge);
        ExpStorage.initCommon();
        XpFluid.initCommon();
        XpObeliskBlock.initCommon();
    }
    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(XOBlockEntityTypes.XP_OBELISK, StorageBlockEntityRenderer::new);
        ModelPredicateProviderRegistry.register(XOItems.XP_REMOVER, new Identifier("active"), (itemStack, clientWorld, livingEntity, count) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return itemStack.hasGlint() ? 0.0F : 1.0F;
        });
        FluidRenderHandlerRegistry.INSTANCE.register(XOFluids.LIQUID_XP_STILL, XOFluids.LIQUID_XP_FLOWING, new SimpleFluidRenderHandler(
          new Identifier("xps:block/xp_still"),
          new Identifier("xps:block/xp_flow"),
          0xCCFF00
        ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), XOFluids.LIQUID_XP_STILL, XOFluids.LIQUID_XP_FLOWING);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), XOBlocks.XP_BERRIES_BUSH, XOBlocks.XP_OBELISK);
    }
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator g) {
        var pack = g.createPack();
        pack.addProvider(XOBlockLootTableProvider::new);
        pack.addProvider(XORecipeProvider::new);
        pack.addProvider(XOChineseProvider::new);
        pack.addProvider(XOModelProvider::new);
        pack.addProvider(XOItemTagProvider::new);
        pack.addProvider(XOBlockTagProvider::new);
    }
}