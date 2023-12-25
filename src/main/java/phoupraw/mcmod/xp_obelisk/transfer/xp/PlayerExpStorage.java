package phoupraw.mcmod.xp_obelisk.transfer.xp;

import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Contract;

public class PlayerExpStorage extends SnapshotParticipant<Double> implements ExpStorage {
    /**
     @param lvl 经验等级
     @return 从0级升到lvl级所需的经验
     */
    @Contract(pure = true)
    public static double lvl2exp(double lvl) {
        double a, b, c;
        if (lvl < 16) {
            a = 1;
            b = 6;
            c = 0;
        } else if (lvl < 31) {
            a = 2.5;
            b = -40.5;
            c = 360;
        } else {
            a = 4.5;
            b = -162.5;
            c = 2220;
        }
        return a * lvl * lvl + b * lvl + c;
    }
    /**
     @param exp 经验点数
     @return 获得exp点经验足够从0级升到多少级
     */
    @Contract(pure = true)
    public static double exp2lvl(double exp) {
        double a, b, c;
        if (exp < 352) {
            a = 1;
            b = 6;
            c = -exp;
        } else if (exp < 1507) {
            a = 2.5;
            b = -40.5;
            c = 360 - exp;
        } else {
            a = 4.5;
            b = -162.5;
            c = 2220 - exp;
        }
        return (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
    }
    public final PlayerEntity player;
    public PlayerExpStorage(PlayerEntity player) {
        this.player = player;
    }
    @Override
    public long insert(ExpSingleton resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        long amount = maxAmount;
        while (amount > 0) {
            int a = Ints.saturatedCast(amount);
            player.addExperience(a);
            amount -= a;
        }
        return amount;
    }
    @Override
    public long extract(ExpSingleton resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        long amount = (Math.min(getAmount(), maxAmount));
        while (amount > 0) {
            int a = Ints.saturatedCast(amount);
            player.addExperience(-a);
            amount -= a;
        }
        return amount;
    }
    @Override
    public long getAmount() {
        return (long) lvl2exp(getDecimalLevel());
    }
    public double getDecimalLevel() {
        return player.experienceLevel + player.experienceProgress;
    }
    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }
    @Override
    protected Double createSnapshot() {
        return getDecimalLevel();
    }
    @Override
    protected void readSnapshot(Double snapshot) {
        int integer = snapshot.intValue();
        double decimal = snapshot - integer;
        player.experienceLevel = integer;
        player.experienceProgress = (float) decimal;
        //player.addExperience(Math.toIntExact(snapshot - getAmount()));
    }
}
