package phoupraw.mcmod.xp_obelisk.block.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.xp_obelisk.misc.XOUtils;

public abstract class NameableBlockEntity extends BlockEntity implements Nameable {
    @Contract(value = "_, _ -> param1", mutates = "param1")
    public static @NotNull NbtCompound put(NbtCompound root, @Nullable Text customName) {
        return XOUtils.put(root, "CustomName", customName);
        //if (customName != null) {
        //    root.putString("CustomName", Text.Serializer.toJson(customName));
        //} else {
        //    root.remove("CustomName");
        //}
        //return root;
    }
    @Contract(mutates = "param1")
    public static void read(NameableBlockEntity self, NbtCompound root) {
        self.setCustomName(XOUtils.getText(root,"CustomName"));
        //self.setCustomName(root.contains("CustomName", NbtElement.STRING_TYPE) ? Text.Serializer.fromJson(root.getString("CustomName")) : null);
    }
    @Getter
    @Setter
    public @Nullable Text customName;
    public NameableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {super(type, pos, state);}
    @Override
    public @NotNull Text getName() {
        Text customName = getCustomName();
        return customName != null ? customName : getCachedState().getBlock().getName();
    }
    @Override
    public void readNbt(NbtCompound root) {
        super.readNbt(root);
        read(this, root);
    }
    @Override
    protected void writeNbt(NbtCompound root) {
        super.writeNbt(root);
        put(root, getCustomName());
    }
}
