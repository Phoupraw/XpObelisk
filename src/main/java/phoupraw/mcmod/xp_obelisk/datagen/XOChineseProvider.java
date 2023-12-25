package phoupraw.mcmod.xp_obelisk.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import phoupraw.mcmod.xp_obelisk.XpObelisk;

public class XOChineseProvider extends FabricLanguageProvider {
    public static final String MOD_NAME = "modmenu.nameTranslation." + XpObelisk.ID;
    public XOChineseProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "zh_cn");
    }
    @Override
    public void generateTranslations(TranslationBuilder b) {
        b.add(MOD_NAME, "经验方尖碑");
        b.add("modmenu.descriptionTranslation." + XpObelisk.ID, "经验方尖碑");
        //b.add(XOBlocks.SOLID_FUEL_GENERATOR, "固体燃料发电机");
        //b.add(XOBlocks.LOW_TRANSFORMER, "低压变压器");
        //b.add(XOBlocks.CARBON_BATTERY, "碳电池");
        //b.add(XOBlocks.REDSTONE_BATTERY, "红石电池");
        //b.add(XOBlocks.ELEC_FURNACE, "电力熔炉");
        //b.add(XOBlocks.ELEC_BLAST, "电力高炉");
        //
        //b.add(XOItems.WRENCH, "扳手");
        //b.add(TIEnchantments.ELEC_MENDING, "电力修补");
    }
}
