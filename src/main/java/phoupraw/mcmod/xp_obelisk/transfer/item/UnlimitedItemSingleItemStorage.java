package phoupraw.mcmod.xp_obelisk.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

import java.util.List;

public class UnlimitedItemSingleItemStorage extends DirectItemSingleItemStorage {
    protected final List<String> path;
    public UnlimitedItemSingleItemStorage(ContainerItemContext context, List<String> path) {
        super(context);
        this.path = path;
    }
    @Override
    protected List<String> getPath() {
        return path;
    }
    @Override
    protected long getCapacity(ItemVariant variant) {
        return Long.MAX_VALUE;
    }
}
