package phoupraw.mcmod.xp_obelisk.block.entity;

import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.xp_obelisk.block.XpObeliskBlock;
import phoupraw.mcmod.xp_obelisk.consts.XOBlockEntityTypes;
import phoupraw.mcmod.xp_obelisk.consts.XOFluids;
import phoupraw.mcmod.xp_obelisk.consts.XOItems;
import phoupraw.mcmod.xp_obelisk.fluid.XpFluid;
import phoupraw.mcmod.xp_obelisk.misc.XOUtils;
import phoupraw.mcmod.xp_obelisk.transfer.AbstractDirectAmountOnlyStorage;
import phoupraw.mcmod.xp_obelisk.transfer.DirectAmountOnlyStorage;
import phoupraw.mcmod.xp_obelisk.transfer.item.UnlimitedDirectSingleItemStorage;
import phoupraw.mcmod.xp_obelisk.transfer.xp.ExpSingleton;
import phoupraw.mcmod.xp_obelisk.transfer.xp.ExpStorage;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class XpObeliskBlockEntity extends NameableBlockEntity implements SidedStorageBlockEntity,TickableBlockEntity {
    public static final Map<Item, SoundEvent> SOUNDS = new HashMap<>(Map.of(
      Items.SCULK, SoundEvents.BLOCK_SCULK_BREAK,
      Items.EXPERIENCE_BOTTLE,SoundEvents.BLOCK_BREWING_STAND_BREW,
      XOItems.XP_BERRIES,SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES
    ));
    static {
        ExpStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.xpStorage, XOBlockEntityTypes.XP_OBELISK);
    }
    public static long getAmount(NbtCompound root) {
        if (root.contains("containerExperience", NbtElement.NUMBER_TYPE)) {
            return root.getLong("containerExperience") * XpFluid.MB_PER_XP;
            //this.liquidXp.amount = (long)root.getInt("containerExperience") * XpFluid.MB_PER_XP;
            //this.liquidXp.variant = FluidVariant.of(XOFluids.XP_STILL);
        } else {
            //this.liquidXp.variant = FluidVariant.fromNbt(root.getCompound("fluidVariant"));
            //this.liquidXp.amount = root.getLong("amount");
            return root.getLong("amount");
        }
    }
    public final AbstractDirectAmountOnlyStorage<ExpSingleton> xpStorage = new XpS();
    public final InsertionOnlyStorage<ItemVariant> itemStorage = new ItemS();
    public final AbstractDirectAmountOnlyStorage<FluidVariant> fluidStorage = new FluidS();
    public @Nullable UUID playerUUID = Util.NIL_UUID;
    public @Nullable Text playerName;
    public boolean vacuum = false;
    public boolean isAuthPlayer = false;
    public XpObeliskBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public XpObeliskBlockEntity(BlockPos pos, BlockState state) {
        this(XOBlockEntityTypes.XP_OBELISK, pos, state);
    }
    @Override
    public @NotNull Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return itemStorage;
    }
    @Override
    public @NotNull Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return fluidStorage;
    }
    @Override
    public void writeNbt(NbtCompound root) {
        super.writeNbt(root);
        //root.put("fluidVariant", this.liquidXp.variant.toNbt());
        XOUtils.put(root, "amount", fluidStorage.getAmount());
        //if (!fluidS.isResourceBlank()) {
        //    root.putLong("amount", fluidS.getAmount());
        //}
        XOUtils.put(root, "player_uuid", playerUUID/*Util.NIL_UUID.equals(playerUUID)?null: NbtHelper.fromUuid(playerUUID)*/);
        //if (!Util.NIL_UUID.equals(playerUUID)) {
        //    root.putUuid("player_uuid", this.playerUUID);
        //}
        XOUtils.put(root, "playerName", playerName);
        //Text playerName = this.playerName;
        //
        //if (playerName != null) {
        //    root.putString("playerName", Text.Serializer.toJson(playerName));
        //}
        XOUtils.put(root, "vacuum", vacuum);
        //if (vacuum) {
        //    root.putBoolean("vacuum", true);
        //}
    }
    @Override
    public void readNbt(NbtCompound root) {
        super.readNbt(root);
        fluidStorage.setAmount(getAmount(root));
        this.playerUUID = XOUtils.getUUID(root, "player_uuid");
        //if (root.contains("playerName", NbtElement.STRING_TYPE)) {
        //    this.playerName = Text.Serializer.fromJson(root.getString("playerName"));
        //} else {
        //    playerName = null;
        //}
        playerName = XOUtils.getText(root, "playerName");
        this.vacuum = root.getBoolean("vacuum");
        World world = getWorld();
        if (world != null) {
            world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
    public void setOwner(@Nullable Entity owner) {
        if (owner == null) {
            playerUUID = null;
            playerName = null;
        } else {
            playerUUID = owner.getUuid();
            playerName = owner.getName();
        }
        markDirty();
    }
    public final SingleVariantStorage<FluidVariant> liquidXp = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }
        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET * Integer.MAX_VALUE;
        }
        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
            if (canInsert(insertedVariant)) {
                long insertedAmount = Math.min(maxAmount, getCapacity(insertedVariant) - amount);

                if (insertedAmount > 0) {
                    updateSnapshots(transaction);

                    if (variant.isBlank()) {
                        variant = FluidVariant.of(XOFluids.LIQUID_XP_STILL);
                        amount = insertedAmount;
                    } else {
                        amount += insertedAmount;
                    }
                }
                return insertedAmount;
            }

            return 0;
        }
        @Override
        protected boolean canInsert(FluidVariant variant) {
            var variant_name = variant.getFluid().getDefaultState().getBlockState().toString();
            return variant_name.contains("liquid_xp") || variant_name.contains("xp_fluid");
        }
        @Override
        protected boolean canExtract(FluidVariant variant) {
            var variant_name = variant.getFluid().getDefaultState().getBlockState().toString();
            boolean isUnlocked = playerUUID.equals(Util.NIL_UUID) || isAuthPlayer;
            return isUnlocked && (variant_name.contains("liquid_xp") || variant_name.contains("xp_fluid"));
        }
        @Override
        protected void onFinalCommit() {
            if (getWorld() != null) {
                getWorld().setBlockState(getPos(), getCachedState().with(XpObeliskBlock.CHARGED, liquidXp.amount != 0));
            }
            isAuthPlayer = false;
            markDirty();
            getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    };
    public void setUuidAndNameTo() {
        setUuidAndNameTo(Util.NIL_UUID, Text.of(""));
    }
    public void setUuidAndNameTo(UUID id, Text name) {
        this.playerUUID = id;
        this.playerName = name;
        this.markDirty();
        //this.toUpdatePacket();
    }
    public String getContainerFillPercentage() {
        float container_progress = (float) ((100.0 / this.liquidXp.getCapacity()) * this.liquidXp.amount);
        return String.format(java.util.Locale.US, "%.7f", container_progress) + "%";
    }
    public void toggleVacuum() {
        this.vacuum = !this.vacuum;
        markDirty();
    }
    public int getContainerExperience() {
        return (int) xpStorage.getAmount();
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }
    //public static void tick(World world, BlockPos pos, BlockState state, XpObeliskBlockEntity sbe) {
    //    if (world == null || world.isClient() || !sbe.vacuum) return;
    //    Vec3d center = new Vec3d(pos.getX(), pos.getY() + 2.5D, pos.getZ());
    //    List<ExperienceOrbEntity> validEntitys = world.getEntitiesByClass(ExperienceOrbEntity.class, Box.of(center, 9D, 5D, 9D), EntityPredicates.VALID_ENTITY);
    //
    //    validEntitys.forEach(experienceOrbEntity -> {
    //        int xp = experienceOrbEntity.getExperienceAmount();
    //        if (xp > 0) {
    //            try (Transaction transaction = Transaction.openOuter()) {
    //                sbe.liquidXp.insert(FluidVariant.of(XOFluids.XP_STILL), (long) xp * XpFluid.MB_PER_XP, transaction);
    //                transaction.commit();
    //            }
    //        }
    //        experienceOrbEntity.discard();
    //    });
    //    boolean charged = sbe.liquidXp.amount != 0;
    //    if (charged ^ state.get(XpObeliskBlock.CHARGED)) {
    //        world.setBlockState(pos, state.with(XpObeliskBlock.CHARGED, charged));
    //    }
    //}
    @Override
    public void tick(World world, BlockPos blockPos, BlockState blockState) {
        if (world == null || world.isClient() || !this.vacuum) return;
        Vec3d center = new Vec3d(blockPos.getX(), blockPos.getY() + 2.5D, blockPos.getZ());
        List<ExperienceOrbEntity> validEntitys = world.getEntitiesByClass(ExperienceOrbEntity.class, Box.of(center, 9D, 5D, 9D), EntityPredicates.VALID_ENTITY);

        validEntitys.forEach(experienceOrbEntity -> {
            int xp = experienceOrbEntity.getExperienceAmount();
            if (xp > 0) {
                try (Transaction transaction = Transaction.openOuter()) {
                    this.liquidXp.insert(FluidVariant.of(XOFluids.LIQUID_XP_STILL), (long) xp * XpFluid.MB_PER_XP, transaction);
                    transaction.commit();
                }
            }
            experienceOrbEntity.discard();
        });
        boolean charged = this.liquidXp.amount != 0;
        if (charged ^ blockState.get(XpObeliskBlock.CHARGED)) {
            world.setBlockState(blockPos, blockState.with(XpObeliskBlock.CHARGED, charged));
        }
    }
    protected class XpS extends AbstractDirectAmountOnlyStorage<ExpSingleton> implements ExpStorage {
        @Override
        public long getAmount() {
            return fluidStorage.getAmount() / XpFluid.MB_PER_XP;
        }
        @Override
        public long getCapacity() {
            return fluidStorage.getCapacity() / XpFluid.MB_PER_XP;
        }
        @Override
        public void setAmount(long amount) {
            fluidStorage.setAmount(amount * XpFluid.MB_PER_XP);
        }
    }
    protected class FluidS extends DirectAmountOnlyStorage<FluidVariant> {
        @Override
        public FluidVariant getResource() {
            return getAmount() == 0 ? FluidVariant.blank() : FluidVariant.of(XOFluids.LIQUID_XP_STILL);
        }
        @Override
        public long getCapacity() {
            return FluidConstants.BUCKET * Integer.MAX_VALUE;
        }
        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
        }
    }
    protected class ItemS extends SnapshotParticipant<Integer> implements InsertionOnlyStorage<ItemVariant> {
        public final List<Item> items = new ArrayList<>();
        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            UnlimitedDirectSingleItemStorage tempSlot = new UnlimitedDirectSingleItemStorage();
            tempSlot.setResource(resource);
            tempSlot.setAmount(maxAmount);
            Storage<ExpSingleton> itemXpS = ExpStorage.ITEM.find(resource.toStack(Ints.saturatedCast(maxAmount)), ContainerItemContext.ofSingleSlot(tempSlot));
            if (StorageUtil.move(itemXpS, xpStorage, Predicates.alwaysTrue(), Long.MAX_VALUE, transaction) != 0) {
                updateSnapshots(transaction);
                items.add(resource.getItem());
            }
            return maxAmount - tempSlot.getAmount();
        }
        @Override
        protected Integer createSnapshot() {
            return items.size();
        }
        @Override
        protected void readSnapshot(Integer snapshot) {
            items.subList(snapshot, items.size()).clear();
        }
        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            World world = getWorld();
            if (world == null) return;
            for (Item item : items) {
                SoundEvent sound = SOUNDS.get(item);
                if (sound == null) continue;
                world.playSound(null, getPos(), sound, SoundCategory.BLOCKS, 1f, 1f);
            }
        }
    }
}
