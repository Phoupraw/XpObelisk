package phoupraw.mcmod.xp_obelisk.transfer.xp;

import com.google.common.base.Predicates;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.xp_obelisk.consts.XOIdentifiers;
import phoupraw.mcmod.xp_obelisk.transfer.AmountOnlyStorage;
import phoupraw.mcmod.xp_obelisk.transfer.EmptySnapshotParticipant;

import java.util.function.BiFunction;

import static phoupraw.mcmod.xp_obelisk.transfer.xp.ExpSingleton.XP;

public interface ExpStorage extends AmountOnlyStorage<ExpSingleton> {
    BlockApiLookup<Storage<ExpSingleton>, @Nullable Direction> SIDED = BlockApiLookup.get(XOIdentifiers.of("exp_storage_sided"), Storage.asClass(), Direction.class);
    ItemApiLookup<Storage<ExpSingleton>, @NotNull ContainerItemContext> ITEM = ItemApiLookup.get(XOIdentifiers.of("exp_storage_item"), Storage.asClass(), ContainerItemContext.class);
    EntityApiLookup<Storage<ExpSingleton>, @Nullable EntityHitResult> ENTITY = EntityApiLookup.get(XOIdentifiers.of("exp_storage_entity"), Storage.asClass(), EntityHitResult.class);
    @SuppressWarnings("unchecked")
    @SafeVarargs
    static <A, C, B extends BlockEntity> void register(BlockApiLookup<A, C> lookup, BiFunction<? super B, ? super C, ? extends A> provider, BlockEntityType<? extends B>... types) {
        lookup.registerForBlockEntities((blockEntity, context) -> provider.apply((B) blockEntity, context), types);
    }
    static ActionResult interact(ItemUsageContext usageContext, @Nullable TransactionContext transaction) {
        try (var t = Transaction.openNested(transaction)) {
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            Storage<ExpSingleton> blockS = SIDED.find(world, pos, usageContext.getSide());
            if (blockS == null) return ActionResult.PASS;
            Storage<ExpSingleton> playerS = ITEM.find(usageContext.getStack(), ContainerItemContext.ofPlayerHand(player, usageContext.getHand()));
            if (playerS == null) return ActionResult.PASS;
            if (StorageUtil.move(blockS, playerS, Predicates.alwaysTrue(), Long.MAX_VALUE, t) != 0) {
                new EmptySnapshotParticipant(()->world.playSound(player, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1, 1)).updateSnapshots(t);
                t.commit();
                return ActionResult.SUCCESS;
            }
            if (StorageUtil.move(playerS, blockS, Predicates.alwaysTrue(), Long.MAX_VALUE, t) != 0) {
                t.commit();
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
    }
    @ApiStatus.Internal
    static void initCommon() {
        register(SIDED, (blockEntity, context) -> new FurnaceExpStorage(blockEntity), BlockEntityType.FURNACE, BlockEntityType.BLAST_FURNACE, BlockEntityType.SMOKER);
        ITEM.registerForItems((itemStack, context) -> new FixedItemExpStorage(context, 9, ItemVariant.of(Items.GLASS_BOTTLE)), Items.EXPERIENCE_BOTTLE);
        ITEM.registerForItems((itemStack, context) -> new FixedItemExpStorage(context, 1, ItemVariant.blank()), Items.SCULK);
        ITEM.registerForItems((itemStack, context) -> new FixedItemExpStorage(context, 5, ItemVariant.blank()), Items.SCULK_CATALYST, Items.SCULK_SENSOR, Items.CALIBRATED_SCULK_SENSOR, Items.SCULK_SHRIEKER);
        ITEM.registerFallback((itemStack, context) -> EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack) != 0 ? new MendingExpStorage(context) : null);
        ENTITY.registerForType((player, context) -> new PlayerExpStorage(player), EntityType.PLAYER);
        ENTITY.registerForType((entity, context) -> new ExpOrbExpStorage(entity), EntityType.EXPERIENCE_ORB);
    }
    @Override
    default ExpSingleton getResource() {
        return XP;
    }
}
