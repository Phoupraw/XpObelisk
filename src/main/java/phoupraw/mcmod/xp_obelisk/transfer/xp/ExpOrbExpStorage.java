package phoupraw.mcmod.xp_obelisk.transfer.xp;

import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.ExperienceOrbEntity;
import phoupraw.mcmod.xp_obelisk.mixin.AExperienceOrbEntity;

public class ExpOrbExpStorage extends SnapshotParticipant<Integer> implements ExpStorage, ExtractionOnlyStorage<ExpSingleton> {
    private final ExperienceOrbEntity orbEntity;
    public ExpOrbExpStorage(ExperienceOrbEntity orbEntity) {this.orbEntity = orbEntity;}
    @Override
    public long extract(ExpSingleton resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        int amount = Ints.saturatedCast(Math.min(maxAmount, getAmount()));
        getOrbEntity().setAmount(amount);
        return amount;
    }
    @Override
    public long getAmount() {
        return getOrbEntity().getExperienceAmount();
    }
    @Override
    public long getCapacity() {
        return getAmount();
    }
    @Override
    protected Integer createSnapshot() {
        return getOrbEntity().getExperienceAmount();
    }
    @Override
    protected void readSnapshot(Integer snapshot) {
        getOrbEntity().setAmount(snapshot);
    }
    @SuppressWarnings("unchecked")
    public <T extends ExperienceOrbEntity & AExperienceOrbEntity> T getOrbEntity() {
        return (T) orbEntity;
    }
}
