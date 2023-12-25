package phoupraw.mcmod.xp_obelisk.consts;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface TIItemTags {
    TagKey<Item> TRANSFORMER = of(XOIdentifiers.TRANSFORMER);
    private static TagKey<Item> of(String path) {
        return of(XOIdentifiers.of(path));
    }
    private static TagKey<Item> of(Identifier id) {
        return TagKey.of(RegistryKeys.ITEM, id);
    }
}
