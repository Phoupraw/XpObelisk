package phoupraw.mcmod.xp_obelisk.misc;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import phoupraw.mcmod.xp_obelisk.XpObelisk;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

//import static phoupraw.mcmod.xp_obelisk.transfer.energy.EnergySingleton.ENERGY;
//@ExtensionMethod(ExtensionMethods.class)
@UtilityClass
public class XOUtils {
    /**
     避免每次都调用{@link Direction#values()}创建新数组。
     */
    public @Unmodifiable List<Direction> DIRECTIONS = List.of(Direction.values());
    /**
     包含一个{@code null}
     */
    public @Unmodifiable List<Direction> N_DIRECTIONS = Lists.asList(null, Direction.values());
    public @Unmodifiable List<EquipmentSlot> EQUIP_SLOTS = List.of(EquipmentSlot.values());
    //public final Random RANDOM = Random.create();
    public LoadingCache<Locale, NumberFormat> NUMBER_FORMAT_CACHE = CacheBuilder.newBuilder().build(CacheLoader.from(XOUtils::newNumberFormat));
    //@Contract(pure = true)
    public double twoPoint(double p) {
        return twoPoint(p, Random.createLocal());
    }
    @Contract(mutates = "param2")
    public double twoPoint(double p, Random random) {
        double integer = Math.floor(p);
        double decimal = p - integer;
        return decimal >= random.nextDouble() ? integer + 1 : integer;
    }
    /**
     @see Inventories#writeNbt(NbtCompound, DefaultedList, boolean)
     */
    @Contract(pure = true)
    public NbtList toNbt(Inventory inventory) {
        NbtList nbtInv = new NbtList();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtSlot = new NbtCompound();
                nbtSlot.putInt("Slot", i);
                itemStack.writeNbt(nbtSlot);
                nbtInv.add(nbtSlot);
            }
        }
        return nbtInv;
    }
    /**
     @see Inventories#readNbt
     */
    @Contract(mutates = "param1")
    public void read(Inventory inventory, NbtList nbtInv) {
        inventory.clear();
        for (NbtElement element : nbtInv) {
            if (element instanceof NbtCompound nbtSlot) {
                int i = nbtSlot.getInt("Slot");
                if (i < inventory.size()) {
                    inventory.setStack(i, ItemStack.fromNbt(nbtSlot));
                } else {
                    XpObelisk.LOGGER.error("i >= inventory.size(), i = %d, inventory.size() = %d, inventory = %s, nbtInv = %s".formatted(i, inventory.size(), inventory, nbtInv));
                }
            } else {
                XpObelisk.LOGGER.error("!(element instanceof NbtCompound), element = %s, nbtInv = %s".formatted(element, nbtInv));
            }
        }
    }
    @Deprecated
    public double floor(double d) {
        if (d <= 0) return 0;
        if (d <= 1) return 1;
        return Math.floor(d);
    }
    public double round(double nu, double de) {
        if (nu <= 0) return 0;
        if (nu <= 1) return 1;
        if (nu >= de - 1 && nu < de) return de - 1;
        return Math.round(nu);
    }
    /**
     stupid java! fuck java!
     */
    public NumberFormat getNumberFormat() {
        return NUMBER_FORMAT_CACHE.getUnchecked(Locale.getDefault());
    }
    public NumberFormat newNumberFormat(Locale locale) {
        NumberFormat nf = NumberFormat.getInstance(locale);
        if (nf instanceof DecimalFormat df) {
            if (locale.equals(Locale.CHINA)) {
                df.setGroupingSize(4);
            }
        }
        return nf;
    }
    public String formatFraction(double numerator, double denominator) {
        NumberFormat numberFormat = getNumberFormat();
        return numberFormat.format(numerator) + "/" + numberFormat.format(denominator);
    }
    @Contract(mutates = "param1")
    public void read(List<? extends SingleItemStorage> slots, NbtList nbtSlots) {
        List<NbtCompound> nbtSlotList = new ArrayList<>(slots.size());
        NbtCompound nbtEmptySlot = new NbtCompound();
        Collections.fill(nbtSlotList, nbtEmptySlot);
        for (int i = 0; i < nbtSlots.size(); i++) {
            NbtCompound nbtSlot = nbtSlots.getCompound(i);
            int slotIndex = nbtSlot.getInt("slot");
            nbtSlotList.set(slotIndex, nbtSlot);
        }
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).readNbt(nbtSlotList.get(i));
        }
    }
    public NbtCompound put(NbtCompound self, String key, StorageView<? extends TransferVariant<?>> slot) {
        //if (!slot.isResourceBlank()) {
        //    self.put(key, toNbt(slot));
        //}
        return XOUtils.put(self, key, toNbt(slot));
    }
    //public NbtCompound put(NbtCompound self, String key, phoupraw.mcmod.trifle_industry.transfter.SingleVariantStorage<? extends TransferVariant<?>> slot) {
    //    //if (!slot.isResourceBlank()) {
    //    //    self.put(key, toNbt(/*(StorageView<TransferVariant<?>>)*/ slot));
    //    //}
    //    return Utils.put(self, key, toNbt(slot));
    //}
    public NbtCompound put(NbtCompound self, String key, List<? extends StorageView<? extends TransferVariant<?>>> slots) {
        NbtList nbtSlots = new NbtList();
        for (var iter = slots.listIterator(); iter.hasNext(); ) {
            int slotIndex = iter.nextIndex();
            var slot = iter.next();
            //if (slot.isResourceBlank()) continue;
            NbtCompound nbtSlot = toNbt(slot);
            if (nbtSlot != null) {
                nbtSlot.putInt("slot", slotIndex);
                nbtSlots.add(nbtSlot);
            }
        }
        //if (!nbtSlots.isEmpty()) {
        //    self.put(key, nbtSlots);
        //}
        return XOUtils.put(self, key, nbtSlots.isEmpty() ? null : nbtSlots);
    }
    public NbtCompound put(NbtCompound self, String key, int value, int defaultValue) {
        //if (value != defaultValue) {
        //    self.putInt(key, value);
        //}
        return XOUtils.put(self, key, value == defaultValue ? null : NbtInt.of(value));
    }
    public NbtCompound put(NbtCompound self, String key, long value) {return put(self, key, value, 0);}
    public NbtCompound put(NbtCompound self, String key, long value, long defaultValue) {
        //if (value != defaultValue) {
        //    self.putLong(key, value);
        //}
        return XOUtils.put(self, key, value == defaultValue ? null : NbtLong.of(value));
    }
    public NbtCompound put(NbtCompound self, String key, Fraction fraction) {
        if (!fraction.equals(Fraction.ZERO)) {
            self.putIntArray(key, new int[]{fraction.getNumerator(), fraction.getDenominator()});
        }
        return self;
    }
    public Fraction getFraction(NbtCompound self, String key) {
        int[] array = self.getIntArray(key);
        if (array.length < 2) return Fraction.ZERO;
        return Fraction.getFraction(array[0], array[1]);
    }
    public void updateNothing(BlockEntity blockEntity) {
        update(blockEntity, 0);
    }
    public void updateRedrew(BlockEntity blockEntity) {
        update(blockEntity, Block.REDRAW_ON_MAIN_THREAD);
    }
    public void update(BlockEntity blockEntity, int flags) {
        blockEntity.markDirty();
        World world = blockEntity.getWorld();
        if (world != null) {
            BlockState state = blockEntity.getCachedState();
            world.updateListeners(blockEntity.getPos(), state, state, flags);
        }
    }
    /**
     如果一个流体的流动纹理和静止纹理相同，可以用此方法快捷创建{@link SimpleFluidRenderHandler}。
     @param textureId 纹理路径，具体写法可以参考{@link SimpleFluidRenderHandler#WATER_STILL}等。
     @param tint 用于给纹理染色的RGB颜色，忽略透明度。
     @return {@link SimpleFluidRenderHandler}
     @see SimpleFluidRenderHandler#SimpleFluidRenderHandler(Identifier, Identifier, int)
     */
    @Contract(value = "_, _ -> new", pure = true)
    @Environment(EnvType.CLIENT)
    public @NotNull FluidRenderHandler fluidTexture(Identifier textureId, int tint) {
        return new SimpleFluidRenderHandler(textureId, textureId, tint);
    }
    public @Nullable NbtCompound toNbt(StorageView<? extends TransferVariant<?>> self) {
        return toNbt(self.getResource(), self.getAmount());
    }
    public @Nullable NbtCompound toNbt(StorageView<? extends TransferVariant<?>> self, boolean force) {
        if (!force && self.isResourceBlank()) return null;
        NbtCompound nbt = new NbtCompound();
        nbt.put("variant", self.getResource().toNbt());
        XOUtils.put(nbt, "amount", self.getAmount());
        //long amount = self.getAmount();
        //if (amount != 0) {
        //    nbt.putLong("amount", amount);
        //}
        return nbt;
    }
    public @Nullable NbtCompound toNbt(TransferVariant<?> variant, long amount) {
        if (variant.isBlank() || amount == 0) return null;
        NbtCompound nbt = new NbtCompound();
        nbt.put("variant", variant.toNbt());
        XOUtils.put(nbt, "amount", amount, 1);
        return nbt;
    }
    public @NotNull ResourceAmount<ItemVariant> itemFrom(@Nullable NbtCompound nbtView) {
        if (nbtView == null || nbtView.isEmpty()) return new ResourceAmount<>(ItemVariant.blank(), 0);
        ItemVariant variant = ItemVariant.fromNbt(nbtView.getCompound("variant"));
        long amount = XOUtils.getLong(nbtView, "amount", 1);
        return new ResourceAmount<>(variant, amount);
    }
    public @NotNull ResourceAmount<FluidVariant> fluidFrom(@Nullable NbtCompound nbtView) {
        if (nbtView == null || nbtView.isEmpty()) return new ResourceAmount<>(FluidVariant.blank(), 0);
        FluidVariant variant = FluidVariant.fromNbt(nbtView.getCompound("variant"));
        long amount = XOUtils.getLong(nbtView, "amount", 1);
        return new ResourceAmount<>(variant, amount);
    }
    @Contract("_, _, _ -> param1")
    public @NotNull NbtCompound put(NbtCompound self, String key, @Nullable NbtElement value) {
        if (value == null) {
            self.remove(key);
        } else {
            self.put(key, value);
        }
        return self;
    }
    @Contract("_, _, _ -> param1")
    public @NotNull NbtCompound put(NbtCompound self, String key, @Nullable UUID value) {
        return put(self, "player_uuid", value == null /*|| Util.NIL_UUID.equals(value)*/ ? null : NbtHelper.fromUuid(value));
    }
    public @Nullable UUID getUUID(NbtCompound self, String key) {
        return self.contains(key, NbtElement.INT_ARRAY_TYPE) ? self.getUuid(key) : null;
    }
    public @NotNull NbtCompound put(NbtCompound self, String key, @Nullable Text value) {
        return put(self, "CustomName", value == null ? null : NbtString.of(Text.Serializer.toJson(value)));
    }
    public @Nullable Text getText(NbtCompound self, String key) {
        return self.contains(key, NbtElement.STRING_TYPE) ? Text.Serializer.fromJson(self.getString(key)) : null;
    }
    @Contract("_, _, _ -> param1")
    public @NotNull NbtCompound put(NbtCompound self, String key, boolean value) {
        return put(self, key, value ? NbtByte.of(true) : null);
    }
    public @Nullable NbtCompound clearDefault(NbtCompound self) {
        for (Iterator<String> iterator = self.getKeys().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            NbtElement element = self.get(key);
            if (element instanceof NbtCompound compound) {
                element = clearDefault(compound);
            } else if (element instanceof NbtList list) {
                element = clearDefault(list);
            }
            if (element == null) {
                iterator.remove();
            }
        }
        return self.isEmpty() ? null : self;
    }
    public @Nullable NbtList clearDefault(NbtList self) {
        for (Iterator<NbtElement> iterator = self.iterator(); iterator.hasNext(); ) {
            NbtElement element = iterator.next();
            if (element instanceof NbtCompound compound) {
                element = clearDefault(compound);
            } else if (element instanceof NbtList list) {
                element = clearDefault(list);
            }
            if (element == null) {
                iterator.remove();
            }
        }
        return self.isEmpty() ? null : self;
    }
    @SuppressWarnings("deprecation")
    public boolean isIn(Item self, TagKey<Item> tagKey) {
        return self.getRegistryEntry().isIn(tagKey);
    }
    public <V> void put(Map<? super ItemConvertible, V> self, ItemConvertible itemOrBlock, V value) {
        self.put(itemOrBlock, value);
        Item item = itemOrBlock.asItem();
        if (item != itemOrBlock && item != Items.AIR) {
            self.put(item, value);
        }
        if (itemOrBlock instanceof BlockItem blockItem) {
            self.put(blockItem.getBlock(), value);
            //if (itemOrBlock instanceof VerticallyAttachableBlockItem wallItem) {
            //    self.put(wallItem.getBlock())
            //}
        }
    }
    public <K, V> @NotNull V get(Map<? super K, ? extends V> self, K key) {
        V value = self.get(key);
        if (value != null) return value;
        throw new NoSuchElementException(Objects.toString(key));
    }
    public boolean isEmpty(Storage<?> self) {
        return !self.nonEmptyIterator().hasNext();
    }
    public <T, S extends Object & Storage<T> & StorageView<T>> boolean isEmpty(S self) {
        return isEmpty((StorageView<?>) self);
    }
    public boolean isEmpty(StorageView<?> self) {
        return self.isResourceBlank() || self.getAmount() == 0;
    }
    public boolean isFull(Iterable<? extends StorageView<?>> self) {
        return Streams.stream(self).allMatch(XOUtils::isFull);
    }
    public boolean isFull(StorageView<?> self) {
        return !self.isResourceBlank() && self.getAmount() == self.getCapacity();
    }
    public long getCapacity(Iterable<? extends StorageView<?>> self) {
        return Streams.stream(self).mapToLong(StorageView::getCapacity).sum();
    }
    public long getAmount(Iterable<? extends StorageView<?>> self) {
        return Streams.stream(self).mapToLong(StorageView::getAmount).sum();
    }
    //public long extract(Storage<EnergySingleton> self, long maxAmount, @Nullable TransactionContext transaction) {
    //    try (var t = Transaction.openNested(transaction)) {
    //        long amount = self.extract(ENERGY, maxAmount, t);
    //        t.commit();
    //        return amount;
    //    }
    //}
    public @Nullable NbtElement get(@Nullable NbtCompound self, List<String> path) {
        if (self == null) return null;
        NbtCompound node = self;
        for (var iterator = path.iterator(); iterator.hasNext(); ) {
            String nodeKey = iterator.next();
            if (!iterator.hasNext()) {
                return node.get(nodeKey);
            }
            if (!node.contains(nodeKey, NbtElement.COMPOUND_TYPE)) {
                break;
            }
            node = node.getCompound(nodeKey);
        }
        return null;
    }
    public @Nullable NbtCompound put(@Nullable NbtCompound self, List<String> path, @Nullable NbtElement value) {
        if (self == null) {
            self = new NbtCompound();
        }
        NbtCompound node = self;
        for (var iterator = path.iterator(); iterator.hasNext(); ) {
            String nodeKey = iterator.next();
            if (!iterator.hasNext()) {
                XOUtils.put(node, nodeKey, value);
            } else {
                NbtCompound nextNode;
                if (node.contains(nodeKey, NbtElement.COMPOUND_TYPE)) {
                    nextNode = node.getCompound(nodeKey);
                } else {
                    nextNode = new NbtCompound();
                    node.put(nodeKey, nextNode);
                }
                node = nextNode;
            }
        }
        return clearDefault(self);
    }
    public @Nullable NbtLong nbtOf(long value) {
        return value == 0 ? null : NbtLong.of(value);
    }
    public @Nullable NbtByte nbtOf(boolean value) {
        return value ? NbtByte.of(true) : null;
    }
    public double getOccupancy(StorageView<?> self) {
        return (double) self.getAmount() / self.getCapacity();
    }
    public double getOccupancy(Iterable<? extends StorageView<?>> self) {
        return (double) getAmount(self) / getCapacity(self);
    }
    public double getOccupancy(SingleSlotStorage<?> self) {
        return getOccupancy((StorageView<?>)self);
    }
    public long getLong(NbtCompound self, String key, long defaultValue) {
        return self.contains(key, NbtElement.NUMBER_TYPE) ? self.getLong(key) : defaultValue;
    }
    public MutableText literal(String text) {
        return Text.translatableWithFallback(text, text);
    }
}
