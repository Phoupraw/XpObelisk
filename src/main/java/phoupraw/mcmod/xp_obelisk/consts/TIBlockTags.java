package phoupraw.mcmod.xp_obelisk.consts;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface TIBlockTags {
    TagKey<Block> TRANSFORMER = of(XOIdentifiers.TRANSFORMER);
    TagKey<Block> MINEABLE_WRENCH = of("mineable/wrench");
    private static TagKey<Block> of(String path) {
        return of(XOIdentifiers.of(path));
    }
    private static TagKey<Block> of(Identifier id) {
        return TagKey.of(RegistryKeys.BLOCK, id);
    }
}
