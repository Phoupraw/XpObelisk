package phoupraw.mcmod.xp_obelisk.misc;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class ListBakedEvent<T> extends RemovableEvent<T> implements Comparator<Identifier> {
    public final Multimap<Identifier, T> listeners = MultimapBuilder.treeKeys(this).arrayListValues(1).build();
    public final SetMultimap<Identifier, Identifier> phaseOrderings = MultimapBuilder.hashKeys().hashSetValues(1).build();
    public ListBakedEvent(Function<@UnmodifiableView Collection<T>, T> invokerFactory) {
        super.invoker = invokerFactory.apply(listeners.values());
    }
    @Override
    public void register(T listener) {
        register(Event.DEFAULT_PHASE, listener);
    }
    @Override
    public void register(Identifier phase, T listener) {
        Objects.requireNonNull(phase, "Tried to register a listener for a null phase!");
        Objects.requireNonNull(listener, "Tried to register a null listener!");
        synchronized (listeners) {
            listeners.put(phase, listener);
        }
    }
    @Override
    public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {
        Objects.requireNonNull(firstPhase, "Tried to add an ordering for a null phase.");
        Objects.requireNonNull(secondPhase, "Tried to add an ordering for a null phase.");
        if (firstPhase.equals(secondPhase)) {
            throw new IllegalArgumentException("Tried to add a phase that depends on itself.");
        }
        synchronized (phaseOrderings) {
            phaseOrderings.put(firstPhase, secondPhase);
            synchronized (listeners) {
                ListMultimap<Identifier, T> copy = MultimapBuilder.treeKeys(this).arrayListValues(1).build(listeners);
                listeners.clear();
                listeners.putAll(copy);
            }
        }
    }
    @Override
    public int compare(Identifier o1, Identifier o2) {
        if (phaseOrderings.containsKey(o1) && phaseOrderings.get(o1).contains(o2)) {
            return 1;
        }
        if (phaseOrderings.containsKey(o2) && phaseOrderings.get(o2).contains(o1)) {
            return -1;
        }
        return 0;
    }
    @Override
    public boolean remove(T listener) {
        return listeners.values().remove(listener);
    }
}
