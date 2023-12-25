package phoupraw.mcmod.xp_obelisk.block;

import com.google.common.base.Predicates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.xp_obelisk.block.entity.XpObeliskBlockEntity;
import phoupraw.mcmod.xp_obelisk.consts.XOIdentifiers;
import phoupraw.mcmod.xp_obelisk.consts.XOItems;
import phoupraw.mcmod.xp_obelisk.item.XpRemoverItem;
import phoupraw.mcmod.xp_obelisk.misc.BlockUsage;
import phoupraw.mcmod.xp_obelisk.misc.XOUtils;
import phoupraw.mcmod.xp_obelisk.transfer.xp.ExpStorage;
import phoupraw.mcmod.xp_obelisk.transfer.xp.PlayerExpStorage;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class XpObeliskBlock extends BlockEntityBlock implements Waterloggable {
    public static final BooleanProperty CHARGED = BooleanProperty.of("charged");
    public static final VoxelShape SHAPE = VoxelShapes.union(
      Block.createCuboidShape(1D, 0D, 1D, 15D, 2D, 15D),
      Block.createCuboidShape(2D, 2D, 2D, 14D, 14D, 14D));
    @Deprecated
    public static final ItemApiLookup<BlockUsage, ItemUsageContext> USAGES = ItemApiLookup.get(XOIdentifiers.BLOCK_XP_OBELISK, BlockUsage.class, ItemUsageContext.class);
    public static final Event<Function<ItemUsageContext, @NotNull ActionResult>> USAGES2 = EventFactory.createArrayBacked(Function.class, usages -> context -> {
        for (var usage : usages) {
            ActionResult result = usage.apply(context);
            if (result != ActionResult.PASS) return result;
        }
        return ActionResult.PASS;
    });
    @ApiStatus.Internal
    public static void initCommon() {
        USAGES2.addPhaseOrdering(XOIdentifiers.INSPECTOR, XOIdentifiers.LOCK);
        USAGES2.addPhaseOrdering(XOIdentifiers.LOCK, XOIdentifiers.KEY);
        USAGES2.register(XOIdentifiers.INSPECTOR, usageContext -> {
            if (!usageContext.getStack().isOf(XOItems.INSPECTOR)) {
                return ActionResult.PASS;
            }
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            if (world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity) {
                UUID playerUUID = blockEntity.playerUUID;
                if (playerUUID != null) {
                    player.sendMessage(Text.translatable("item.tooltip.owner", blockEntity.playerName), false);
                    player.sendMessage(Text.literal("UUID: " + playerUUID), false);
                } else {
                    player.sendMessage(Text.translatable("item.debug_info.xp.container_no_owner"), false);
                }
                player.sendMessage(Text.translatable("item.debug_info.xp.container_info", blockEntity.xpStorage.getAmount(), Integer.MAX_VALUE), false);
                player.sendMessage(Text.translatable("item.debug_info.xp.container_fill", DecimalFormat.getPercentInstance().format(XOUtils.getOccupancy(blockEntity.fluidStorage))), false);
                if (blockEntity.vacuum) {
                    player.sendMessage(Text.translatable("text.storageBlock.vacuum"), false);
                }
            }
            player.sendMessage(Text.translatable("item.debug_info.xp.player_info", PlayerExpStorage.lvl2exp(player.experienceLevel + player.experienceProgress)), false);
            world.playSound(player, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 0.5f, 1f);
            return ActionResult.SUCCESS;
        });
        //USAGES.registerForItems(new ConstItemApiProvider<>(usageOf(false, (world, player, blockEntity) -> {
        //    UUID playerUUID = blockEntity.playerUUID;
        //    if (playerUUID != null) {
        //        player.sendMessage(Text.translatable("item.tooltip.owner", blockEntity.playerName), false);
        //        player.sendMessage(Text.literal("UUID: " + playerUUID), false);
        //    } else {
        //        player.sendMessage(Text.translatable("item.debug_info.xp.container_no_owner"), false);
        //    }
        //    player.sendMessage(Text.translatable("item.debug_info.xp.container_info", blockEntity.xpStorage.getAmount(), Integer.MAX_VALUE), false);
        //    player.sendMessage(Text.translatable("item.debug_info.xp.container_fill", DecimalFormat.getPercentInstance().format(XOUtils.getOccupancy(blockEntity.fluidStorage))), false);
        //    if (blockEntity.vacuum) {
        //        player.sendMessage(Text.translatable("text.storageBlock.vacuum"), false);
        //    }
        //    player.sendMessage(Text.translatable("item.debug_info.xp.player_info", PlayerExpStorage.lvl2exp(player.experienceLevel + player.experienceProgress)), false);
        //    world.playSound(player, blockEntity.getPos(), SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 0.5f, 1f);
        //    return ActionResult.SUCCESS;
        //})), XOItems.INSPECTOR);
        //USAGES.registerForItems(new ConstItemApiProvider<>((block, usageContext) -> {
        //    PlayerEntity player = usageContext.getPlayer();
        //    if (player == null) return ActionResult.PASS;
        //    World world = usageContext.getWorld();
        //    BlockPos pos = usageContext.getBlockPos();
        //    if (world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity) {
        //        UUID playerUUID = blockEntity.playerUUID;
        //        if (playerUUID != null) {
        //            player.sendMessage(Text.translatable("item.tooltip.owner", blockEntity.playerName), false);
        //            player.sendMessage(Text.literal("UUID: " + playerUUID), false);
        //        } else {
        //            player.sendMessage(Text.translatable("item.debug_info.xp.container_no_owner"), false);
        //        }
        //        player.sendMessage(Text.translatable("item.debug_info.xp.container_info", blockEntity.xpStorage.getAmount(), Integer.MAX_VALUE), false);
        //        player.sendMessage(Text.translatable("item.debug_info.xp.container_fill", DecimalFormat.getPercentInstance().format(XOUtils.getOccupancy(blockEntity.fluidStorage))), false);
        //        if (blockEntity.vacuum) {
        //            player.sendMessage(Text.translatable("text.storageBlock.vacuum"), false);
        //        }
        //    }
        //    player.sendMessage(Text.translatable("item.debug_info.xp.player_info", PlayerExpStorage.lvl2exp(player.experienceLevel + player.experienceProgress)), false);
        //    world.playSound(player, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 0.5f, 1f);
        //    return ActionResult.SUCCESS;
        //}), XOItems.INSPECTOR);
        //USAGES.registerForItems(new ConstItemApiProvider<>(usageOf(false, (world, player, blockEntity) -> {
        //    if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //    PlayerEntity player = usageContext.getPlayer();
        //    if (player == null) return ActionResult.PASS;
        //    World world = usageContext.getWorld();
        //    BlockPos pos = usageContext.getBlockPos();
        //    if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
        //        return ActionResult.FAIL;
        //    }
        //    blockEntity.setOwner(null);
        //    player.sendMessage(Text.translatable("text.storageBlock.isOpen"), true);
        //    world.playSound(player, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);
        //    if (!player.isCreative()) {
        //        player.getStackInHand(usageContext.getHand()).decrement(1);
        //    }
        //    return ActionResult.SUCCESS;
        //}), XOItems.KEY);
        //USAGES.registerForItems(new ConstItemApiProvider<>((block, usageContext) -> {
        //    if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //    PlayerEntity player = usageContext.getPlayer();
        //    if (player == null) return ActionResult.PASS;
        //    World world = usageContext.getWorld();
        //    BlockPos pos = usageContext.getBlockPos();
        //    if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
        //        return ActionResult.FAIL;
        //    }
        //    blockEntity.setOwner(null);
        //    player.sendMessage(Text.translatable("text.storageBlock.isOpen"), true);
        //    world.playSound(player, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);
        //    if (!player.isCreative()) {
        //        player.getStackInHand(usageContext.getHand()).decrement(1);
        //    }
        //    return ActionResult.SUCCESS;
        //}), XOItems.KEY);
        USAGES2.register(XOIdentifiers.LOCK, usageContext -> {
            if (!tryAccess(usageContext)) return ActionResult.FAIL;
            return ActionResult.PASS;
        });
        USAGES2.register(XOIdentifiers.KEY, usageContext -> {
            if (!usageContext.getStack().isOf(XOItems.KEY)) {
                return ActionResult.PASS;
            }
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
                return ActionResult.FAIL;
            }
            blockEntity.setOwner(null);
            player.sendMessage(Text.translatable("text.storageBlock.isOpen"), true);
            world.playSound(player, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);
            if (!player.isCreative()) {
                player.getStackInHand(usageContext.getHand()).decrement(1);
            }
            return ActionResult.SUCCESS;
        });
        USAGES2.register(XOIdentifiers.KEY, usageContext -> {
            if (!usageContext.getStack().isOf(XOItems.LOCK)) {
                return ActionResult.PASS;
            }
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
                return ActionResult.FAIL;
            }
            if (blockEntity.playerUUID != null) {
                player.sendMessage(Text.translatable("text.storageBlock.isLocked"), true);
                world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1f, 1f);
                return ActionResult.FAIL;
            }
            blockEntity.setOwner(player);
            player.sendMessage(Text.translatable("text.storageBlock.locked"), true);
            world.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);
            usageContext.getStack().decrement(1);
            return ActionResult.SUCCESS;
        });
        //USAGES.registerForItems(new ConstItemApiProvider<>((block, usageContext) -> {
        //    if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //    PlayerEntity player = usageContext.getPlayer();
        //    if (player == null) return ActionResult.PASS;
        //    World world = usageContext.getWorld();
        //    BlockPos pos = usageContext.getBlockPos();
        //    if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
        //        return ActionResult.FAIL;
        //    }
        //    if (blockEntity.playerUUID != null) {
        //        player.sendMessage(Text.translatable("text.storageBlock.isLocked"), true);
        //        world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1f, 1f);
        //        return ActionResult.FAIL;
        //    }
        //    blockEntity.setOwner(player);
        //    player.sendMessage(Text.translatable("text.storageBlock.locked"), true);
        //    world.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);
        //    usageContext.getStack().decrement(1);
        //    return ActionResult.SUCCESS;
        //}), XOItems.LOCK);
        //USAGES.registerForItems(new ConstItemApiProvider<>((block, usageContext) -> {
        //    if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //    PlayerEntity player = usageContext.getPlayer();
        //    if (player == null) return ActionResult.PASS;
        //    World world = usageContext.getWorld();
        //    BlockPos pos = usageContext.getBlockPos();
        //    if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
        //        return ActionResult.FAIL;
        //    }
        //    ItemStack stack = usageContext.getStack();
        //    int count = stack.getCount();
        //    NbtCompound root = stack.getNbt();
        //    PlayerExpStorage playerExpS = new PlayerExpStorage(player);
        //    if (root != null && root.getBoolean(XpRemoverItem.MODE)) {
        //        if (StorageUtil.move(blockEntity.xpStorage, playerExpS, Predicates.alwaysTrue(), (long) PlayerExpStorage.lvl2exp(player.experienceLevel + count) - playerExpS.getAmount(), null) != 0) {
        //            world.playSound(player, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 0.125f, 1f);
        //            return ActionResult.SUCCESS;
        //        }
        //    } else {
        //        long amount = playerExpS.getAmount() - (long) PlayerExpStorage.lvl2exp(Math.ceil(player.experienceLevel + player.experienceProgress - count));
        //        if (amount == 0) {
        //            amount = playerExpS.getAmount() - (long) PlayerExpStorage.lvl2exp(player.experienceLevel - 1);
        //        }
        //        if (StorageUtil.move(playerExpS, blockEntity.xpStorage, Predicates.alwaysTrue(), amount, null) != 0) {
        //            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.125f, 1f);
        //            return ActionResult.SUCCESS;
        //        }
        //    }
        //    return ActionResult.FAIL;
        //}), XOItems.XP_REMOVER);
        USAGES2.register(XOIdentifiers.KEY, usageContext -> {
            if (!usageContext.getStack().isOf(XOItems.XP_REMOVER)) {
                return ActionResult.PASS;
            }
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
                return ActionResult.FAIL;
            }
            ItemStack stack = usageContext.getStack();
            int count = stack.getCount();
            NbtCompound root = stack.getNbt();
            PlayerExpStorage playerExpS = new PlayerExpStorage(player);
            if (root != null && root.getBoolean(XpRemoverItem.MODE)) {
                if (StorageUtil.move(blockEntity.xpStorage, playerExpS, Predicates.alwaysTrue(), (long) PlayerExpStorage.lvl2exp(player.experienceLevel + count) - playerExpS.getAmount(), null) != 0) {
                    world.playSound(player, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 0.125f, 1f);
                    return ActionResult.SUCCESS;
                }
            } else {
                long amount = playerExpS.getAmount() - (long) PlayerExpStorage.lvl2exp(Math.ceil(player.experienceLevel + player.experienceProgress - count));
                if (amount == 0) {
                    amount = playerExpS.getAmount() - (long) PlayerExpStorage.lvl2exp(player.experienceLevel - 1);
                }
                if (StorageUtil.move(playerExpS, blockEntity.xpStorage, Predicates.alwaysTrue(), amount, null) != 0) {
                    world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.125f, 1f);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.SUCCESS;
        });
        USAGES2.register(XOIdentifiers.KEY, usageContext -> {
            if (!usageContext.getStack().isOf(Items.REDSTONE_TORCH)) {
                return ActionResult.PASS;
            }
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
                return ActionResult.FAIL;
            }
            blockEntity.vacuum ^= true;
            blockEntity.markDirty();
            world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        });
        //USAGES.registerForItems(new ConstItemApiProvider<>((block, usageContext) -> {
        //    if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //    PlayerEntity player = usageContext.getPlayer();
        //    if (player == null) return ActionResult.PASS;
        //    World world = usageContext.getWorld();
        //    BlockPos pos = usageContext.getBlockPos();
        //    if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
        //        return ActionResult.FAIL;
        //    }
        //    blockEntity.vacuum ^= true;
        //    blockEntity.markDirty();
        //    world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
        //    return ActionResult.SUCCESS;
        //}), Items.REDSTONE_TORCH);
        USAGES2.register(XOIdentifiers.KEY, usageContext -> {
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
                return ActionResult.FAIL;
            }
            if (StorageUtil.move(PlayerInventoryStorage.of(player).getHandSlot(usageContext.getHand()), blockEntity.itemStorage, Predicates.alwaysTrue(), 1, null) == 1) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        //USAGES.registerFallback((itemStack, context) -> {
        //    if (!tryAccess(context)) return null;
        //    //return ExpStorage.interact(usageContext);
        //    PlayerEntity player = context.getPlayer();
        //    if (player == null) return null;
        //    World world = context.getWorld();
        //    BlockPos pos = context.getBlockPos();
        //    if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
        //        return null;
        //    }
        //    try (var t = Transaction.openOuter()) {
        //        if (StorageUtil.move(PlayerInventoryStorage.of(player).getHandSlot(context.getHand()), blockEntity.itemStorage, Predicates.alwaysTrue(), 1, t) == 0) {
        //            return null;
        //        }
        //    }
        //    return (block, usageContext) -> {
        //        if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //        StorageUtil.move(PlayerInventoryStorage.of(player).getHandSlot(usageContext.getHand()), blockEntity.itemStorage, Predicates.alwaysTrue(), 1, null);
        //        return ActionResult.SUCCESS;
        //    };
        //});
        USAGES2.register(XOIdentifiers.KEY, usageContext -> {
            PlayerEntity player = usageContext.getPlayer();
            if (player == null) return ActionResult.PASS;
            World world = usageContext.getWorld();
            BlockPos pos = usageContext.getBlockPos();
            if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
                return ActionResult.FAIL;
            }
            ItemStack itemStack = usageContext.getStack();
            Storage<FluidVariant> itemFluidS = FluidStorage.ITEM.find(itemStack, ContainerItemContext.withConstant(itemStack));
            if (itemFluidS == null) return ActionResult.PASS;
            return FluidStorageUtil.interactWithFluidStorage(blockEntity.fluidStorage, player, usageContext.getHand()) ? ActionResult.SUCCESS : ActionResult.FAIL;
        });
        //USAGES.registerFallback((itemStack, context) -> {
        //    Storage<FluidVariant> itemFluidS = FluidStorage.ITEM.find(itemStack, ContainerItemContext.withConstant(itemStack));
        //    if (itemFluidS == null) return null;
        //    return (block, usageContext) -> {
        //        if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //        PlayerEntity player = usageContext.getPlayer();
        //        if (player == null) return ActionResult.PASS;
        //        World world = usageContext.getWorld();
        //        BlockPos pos = usageContext.getBlockPos();
        //        if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) {
        //            return ActionResult.FAIL;
        //        }
        //        return FluidStorageUtil.interactWithFluidStorage(blockEntity.fluidStorage, player, usageContext.getHand()) ? ActionResult.SUCCESS : ActionResult.FAIL;
        //    };
        //});
        USAGES2.register(XOIdentifiers.KEY, usageContext -> ExpStorage.interact(usageContext, null));
        //USAGES.registerFallback((itemStack, context) -> {
        //    try (var t = Transaction.openOuter()) {
        //        if (ExpStorage.interact(context, t).isAccepted()) {
        //            return (block, usageContext) -> {
        //                if (!tryAccess(usageContext)) return ActionResult.FAIL;
        //                return ExpStorage.interact(usageContext, null);
        //            };
        //        }
        //    }
        //    return null;
        //});
    }
    public static boolean tryAccess(ItemUsageContext usageContext) {
        PlayerEntity player = usageContext.getPlayer();
        if (player == null) return false;
        World world = usageContext.getWorld();
        BlockPos pos = usageContext.getBlockPos();
        if (world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity) {
            UUID playerUUID = blockEntity.playerUUID;
            if (playerUUID != null && !player.getUuid().equals(playerUUID) && !player.isCreative()) {
                player.sendMessage(Text.translatable("text.storageBlock.denied"), true);
                world.playSound(player, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1f, 1f);
                return false;
            }
        }
        return true;
    }
    //public static BlockUsage usageOf(boolean checkAccess, TriFunction<World, PlayerEntity, XpObeliskBlockEntity, ActionResult> usage) {
    //    return (block, usageContext) -> {
    //        if (checkAccess && !tryAccess(usageContext)) return ActionResult.FAIL;
    //        PlayerEntity player = usageContext.getPlayer();
    //        if (player == null) return ActionResult.PASS;
    //        World world = usageContext.getWorld();
    //        BlockPos pos = usageContext.getBlockPos();
    //        return world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity ? usage.apply(world, player, blockEntity) : ActionResult.FAIL;
    //    };
    //}
    //public static final BooleanProperty VACUUM = BooleanProperty.of("vacuum");
    public XpObeliskBlock(Settings settings) {
        super(settings.nonOpaque());
        setDefaultState(getDefaultState()
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(CHARGED, false)
            .with(Properties.WATERLOGGED, false)
          /*.with(VACUUM, false)*/);
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean waterlogged = fluidState.getFluid() == Fluids.WATER;
        NbtCompound blockEntityNbt = BlockItem.getBlockEntityNbt(ctx.getStack());
        boolean charged = blockEntityNbt != null && XpObeliskBlockEntity.getAmount(blockEntityNbt) != 0;
        BlockState blockState = getDefaultState().with(Properties.WATERLOGGED, waterlogged).with(CHARGED, charged);
        PlayerEntity player = ctx.getPlayer();
        Direction facing = player != null && player.isSneaking() ? ctx.getHorizontalPlayerFacing() : ctx.getHorizontalPlayerFacing().getOpposite();
        return blockState.with(Properties.HORIZONTAL_FACING, facing);
        // Place Block the same Direction as the Player when sneaking
        //if (Objects.requireNonNull(ctx.getPlayer()).isSneaking()) {
        //    return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing()).with(Properties.WATERLOGGED, waterlogged);
        //}
        // Place Block the opposite Direction as the Player
        //return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(Properties.WATERLOGGED, waterlogged);
    }
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            //world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            //world.getFluidTickScheduler().isTicking(pos, Fluids.WATER);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }
    //public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
    //    //if (! state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
    //    //    BlockState blockState = state.with(Properties.WATERLOGGED, true);
    //    //
    //    //    world.setBlockState(pos, blockState, 3);
    //    //
    //    //    //world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
    //    //    //world.createAndScheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
    //    //    world.getFluidTickScheduler().isTicking(pos, Fluids.WATER);
    //    //    return true;
    //    //} else {
    //    //    return false;
    //    //}
    //    return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    //}
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }
    //@Override
    //public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    //    //if (!world.isClient()) {
    //    //    XpObeliskBlockEntity tile = (XpObeliskBlockEntity) world.getBlockEntity(pos);
    //    //    if (tile == null) {return;}
    //    //
    //    //    ItemStack drop = new ItemStack(asItem());
    //    //
    //    //    if (tile.liquidXp.amount > 0 || !tile.playerUUID.equals(Util.NIL_UUID)) {
    //    //        NbtCompound stackTag = new NbtCompound();
    //    //        //stackTag.put(ModBlocks.TAG_ID, tile.writeNbt(new NbtCompound()));
    //    //
    //    //        //stackTag.put(ModBlocks.TAG_ID, tile.getNbtData());
    //    //        drop.setNbt(stackTag);
    //    //        //data get entity @s SelectedItem
    //    //    }
    //    //
    //    //    dropStack(world, pos, drop);
    //    //}
    //
    //    super.onBreak(world, pos, state, player);
    //}
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(CHARGED);
        stateManager.add(Properties.WATERLOGGED);
        //stateManager.add(VACUUM);
    }
    //@Override
    //public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    //    if (!world.isClient()) {
    //        final XpObeliskBlockEntity tile = (XpObeliskBlockEntity) world.getBlockEntity(pos);
    //        if (tile == null) {return;}
    //
    //        if (tile.liquidXp.amount != 0) {
    //            world.setBlockState(pos, state.with(CHARGED, true));
    //        }
    //    }
    //    super.onPlaced(world, pos, state, placer, itemStack);
    //}
    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (!(world.getBlockEntity(pos) instanceof XpObeliskBlockEntity blockEntity)) return;
        if (blockEntity.vacuum) {

            double targetX = pos.getX() + 0.5D;
            double targetY = pos.getY() + 0.5D;
            double targetZ = pos.getZ() + 0.5D;

            double offsetX = 1D - random.nextInt(3);
            double offsetZ = 1D - random.nextInt(3);

            world.addParticle(ParticleTypes.PORTAL, targetX, targetY, targetZ, offsetX, 0.1, offsetZ);
        }
    }
    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stackInHand = player.getStackInHand(hand);
        ItemUsageContext context = new ItemUsageContext(world, player, hand, stackInHand, hit);
        //BlockUsage usage = USAGES.find(stackInHand, context);
        //return usage == null ? ActionResult.PASS : usage.apply(this, context);
        return USAGES2.invoker().apply(context);
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new XpObeliskBlockEntity(pos, state);
    }
}
