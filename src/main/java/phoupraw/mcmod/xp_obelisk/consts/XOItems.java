package phoupraw.mcmod.xp_obelisk.consts;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import phoupraw.mcmod.xp_obelisk.XpObelisk;
import phoupraw.mcmod.xp_obelisk.item.*;
import phoupraw.mcmod.xp_obelisk.transfer.xp.ExpStorage;
import phoupraw.mcmod.xp_obelisk.transfer.xp.FixedItemExpStorage;

//import phoupraw.mcmod.xp_obelisk.transfer.energy.BatteryAttr;
@ApiStatus.NonExtendable
public interface XOItems {
    BlockItem XP_OBELISK = r(XOIdentifiers.BLOCK_XP_OBELISK, new BlockItem(XOBlocks.XP_OBELISK, new FabricItemSettings().maxCount(16).rarity(Rarity.RARE)));
    BlockItem SOUL_COPPER_BLOCK = r(XOIdentifiers.BLOCK_SOUL_COPPER, new BlockItem(XOBlocks.SOUL_COPPER_BLOCK, new FabricItemSettings().maxCount(64)));
    Item XP_ROD = r(XOIdentifiers.XP_ROD,new Item(new FabricItemSettings()));
    Item SOUL_COPPER_BLEND = r(XOIdentifiers.SOUL_COPPER_BLEND,new Item(new FabricItemSettings()));
    Item SOUL_COPPER_INGOT = r(XOIdentifiers.SOUL_COPPER_INGOT,new Item(new FabricItemSettings()));
    Item SOUL_COPPER_NUGGET = r(XOIdentifiers.SOUL_COPPER_NUGGET,new Item(new FabricItemSettings()));
    Item XP_BERRIES_SEEDS = r(XOIdentifiers.XP_BERRIES_SEEDS, new AliasedBlockItem(XOBlocks.XP_BERRIES_BUSH, new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    Item BUCKETED_LIQUID_XP = r(XOIdentifiers.XP_BUCKET, new BucketItem(XOFluids.LIQUID_XP_STILL, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    Item INSPECTOR = r(XOIdentifiers.INSPECTOR, new MoreInfoItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    Item LOCK = r(XOIdentifiers.LOCK, new MoreInfoItem(new FabricItemSettings().maxCount(16).rarity(Rarity.UNCOMMON)));
    Item KEY = r(XOIdentifiers.KEY, new MoreInfoItem(new FabricItemSettings().maxCount(16).rarity(Rarity.UNCOMMON)));
    Item XP_REMOVER = r(XOIdentifiers.XP_REMOVER, new XpRemoverItem(new FabricItemSettings()));
    Item XP_DUST = r(XOIdentifiers.XP_DUST, new HasGlinttem(new FabricItemSettings()));
    Item XP_BERRIES = r(XOIdentifiers.XP_BERRIES, new XpBerriesItem(new FabricItemSettings().food(new FoodComponent.Builder().hunger(2).saturationModifier(1f).snack().alwaysEdible().build())));
    Item HANDBOOK = r(XOIdentifiers.HANDBOOK, new HandBookItem(new FabricItemSettings()));
    //BlockItem SOLID_FUEL_GENERATOR = r(TIIdentifiers.SOLID_FUEL_GENERATOR, new BlockItem(XOBlocks.SOLID_FUEL_GENERATOR, new FabricItemSettings()));
    //BlockItem ELEC_FURNACE = r(TIIdentifiers.ELEC_FURNACE, new BlockItem(XOBlocks.ELEC_FURNACE, new FabricItemSettings()));
    //BlockItem ELEC_BLAST = r(TIIdentifiers.ELEC_BLAST, new BlockItem(XOBlocks.ELEC_BLAST, new FabricItemSettings()));
    //BlockItem CARBON_BATTERY = r(TIIdentifiers.CARBON_BATTERY, new BatteryItem(XOBlocks.CARBON_BATTERY, new FabricItemSettings().maxCount(4)));
    //BlockItem REDSTONE_BATTERY = r(TIIdentifiers.REDSTONE_BATTERY, new BatteryItem(XOBlocks.REDSTONE_BATTERY, new FabricItemSettings().maxCount(4)));
    //BlockItem LOW_TRANSFORMER = r(TIIdentifiers.LOW_TRANSFORMER, new BlockItem(XOBlocks.LOW_TRANSFORMER, new FabricItemSettings()));
    //BlockItem BUCKET = r(TIIdentifiers.BUCKET, new BucketBlockItem(XOBlocks.BUCKET, new FabricItemSettings().maxCount(16)));
    //Item WRENCH = r(TIIdentifiers.WRENCH, new WrenchItem(new FabricItemSettings().maxCount(1)));
    //Item DRILL = r(TIIdentifiers.DRILL, new LowTierDrillItem(new FabricItemSettings().maxCount(1)));
    ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, XOIdentifiers.of(XpObelisk.ID), FabricItemGroup.builder()
      .displayName(XP_OBELISK.getName())
      .icon(XOItems.XP_OBELISK::getDefaultStack)
      .entries(XOItems::addItemGroupEntries)
      .build());
    @ApiStatus.Internal
    static void initCommon() {
        CompostingChanceRegistry.INSTANCE.add(XP_BERRIES, 0.75f);
        CompostingChanceRegistry.INSTANCE.add(XP_BERRIES_SEEDS, 0.65f);
        ExpStorage.ITEM.registerForItems((itemStack, context) -> new FixedItemExpStorage(context, XpBerriesItem.XP_PER_BERRIE, ItemVariant.blank()), XP_BERRIES);
    }
    private static void addItemGroupEntries(ItemGroup.DisplayContext context, ItemGroup.Entries entries) {
        for (Item item : new Item[]{XP_OBELISK,XP_BERRIES,XP_ROD,SOUL_COPPER_BLEND,SOUL_COPPER_INGOT,SOUL_COPPER_NUGGET,XP_REMOVER,INSPECTOR,LOCK,XP_DUST,KEY}) {
            entries.add(item);
        }
        if (HandBookItem.isPatchouliLoaded()) {
            entries.add(HANDBOOK);
        }
        for (Item item : new Item[]{SOUL_COPPER_BLOCK,XP_BERRIES_SEEDS, BUCKETED_LIQUID_XP}) {
            entries.add(item);
        }
        //for (Item item : new Item[]{CARBON_BATTERY, REDSTONE_BATTERY}) {
        //    entries.add(item);
        //    ItemStack stack = item.getDefaultStack();
        //    stack.setNbt(BatteryItem.setAmount(stack.getNbt(), BatteryAttr.getCapacity(item)));
        //    //try (var t = Transaction.openOuter()) {
        //    //    ContainerItemContext cic = ContainerItemContext.ofSingleSlot(InventoryStorage.of(new SimpleInventory(stack), null).getSlot(0));
        //    //    new BatteryItemStorage(cic, stack).insert(EnergySingleton.ES, Long.MAX_VALUE, t);
        //    //    entries.add(cic.getItemVariant().toStack());
        //    //}
        //    entries.add(stack);
        //}
        //for (Item item : new Item[]{WRENCH, DRILL}) {
        //    entries.add(item);
        //}
        //for (Item item : new Item[]{REDSTONE_BATTERY}) {
        //    entries.add(item);
        //    ItemStack stack = item.getDefaultStack();
        //    EnergyStorageItem.setEnergy(stack, EnergyStorageItem.getEnergyCapacity(stack));
        //    entries.add(stack);
        //}
    }
    @Contract("_, _ -> param2")
    private static <T extends Item> T r(Identifier id, T item) {
        return Registry.register(Registries.ITEM, id, item);
    }
}
