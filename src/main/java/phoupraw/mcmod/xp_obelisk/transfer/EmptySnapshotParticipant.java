package phoupraw.mcmod.xp_obelisk.transfer;

import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
@RequiredArgsConstructor
public class EmptySnapshotParticipant extends SnapshotParticipant<Object> {
    public final Runnable finalCommit;
    @Override
    protected Object createSnapshot() {
        return EmptySnapshotParticipant.class;
    }
    @Override
    protected void readSnapshot(Object snapshot) {

    }
    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        finalCommit.run();
    }
}
