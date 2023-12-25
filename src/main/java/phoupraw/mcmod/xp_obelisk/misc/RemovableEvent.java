package phoupraw.mcmod.xp_obelisk.misc;

import net.fabricmc.fabric.api.event.Event;

@SuppressWarnings("NonExtendableApiUsage")
public abstract class RemovableEvent<T> extends Event<T> {
    public abstract boolean remove(T listener);
}
