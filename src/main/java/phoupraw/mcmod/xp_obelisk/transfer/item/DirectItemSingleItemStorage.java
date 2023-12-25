package phoupraw.mcmod.xp_obelisk.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.nbt.NbtCompound;
import phoupraw.mcmod.xp_obelisk.misc.XOUtils;
import phoupraw.mcmod.xp_obelisk.transfer.ItemSingleVariantStorage;

import java.util.List;

public abstract class DirectItemSingleItemStorage extends ItemSingleVariantStorage<ItemVariant> {
    public DirectItemSingleItemStorage(ContainerItemContext context) {
        super(context);
    }
    @Override
    protected ItemVariant getBlankResource() {
        return ItemVariant.blank();
    }
    @Override
    protected ItemVariant getResource(ItemVariant currentVariant) {
        return XOUtils.get(currentVariant.getNbt(), getPath()) instanceof NbtCompound nbtView ? XOUtils.itemFrom(nbtView).resource() : getBlankResource();
        //NbtCompound node = currentVariant.getNbt();
        //if (node == null) return getBlankResource();
        //List<String> nodeKeys = getPath();
        //for (String nodeKey : nodeKeys) {
        //    if (!node.contains(nodeKey, NbtElement.COMPOUND_TYPE)) {
        //        return getBlankResource();
        //    }
        //    node = node.getCompound(nodeKey);
        //}
        //return Utils.itemFrom(node).resource();
    }
    @Override
    protected long getAmount(ItemVariant currentVariant) {
        return XOUtils.get(currentVariant.getNbt(), getPath()) instanceof NbtCompound nbtView ? XOUtils.itemFrom(nbtView).amount() : 0;
        //NbtCompound node = currentVariant.getNbt();
        //if (node == null) return 0;
        //List<String> nodeKeys = getPath();
        //for (String nodeKey : nodeKeys) {
        //    if (!node.contains(nodeKey, NbtElement.COMPOUND_TYPE)) {
        //        return 0;
        //    }
        //    node = node.getCompound(nodeKey);
        //}
        //return Utils.itemFrom(node).amount();
    }
    @Override
    protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, ItemVariant newResource, long newAmount) {
        return ItemVariant.of(currentVariant.getItem(), XOUtils.put(currentVariant.copyOrCreateNbt(), getPath(), XOUtils.toNbt(newResource, newAmount)));
        //NbtCompound root = currentVariant.copyOrCreateNbt();
        //NbtCompound node = root;
        //List<String> nodeKeys = getPath();
        //for (var iterator = nodeKeys.iterator(); iterator.hasNext(); ) {
        //    String nodeKey = iterator.next();
        //    if (!iterator.hasNext()) {
        //        Utils.put(node, nodeKey, Utils.toNbt(newResource, newAmount));
        //    } else {
        //        NbtCompound nextNode;
        //        if (node.contains(nodeKey, NbtElement.COMPOUND_TYPE)) {
        //            nextNode = node.getCompound(nodeKey);
        //        } else {
        //            nextNode = new NbtCompound();
        //            node.put(nodeKey, nextNode);
        //        }
        //        node = nextNode;
        //    }
        //}
        //return ItemVariant.of(currentVariant.getItem(), Utils.clearDefault(root));
    }
    protected abstract List<String> getPath();
}
