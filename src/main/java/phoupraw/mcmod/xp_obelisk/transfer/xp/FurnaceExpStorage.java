package phoupraw.mcmod.xp_obelisk.transfer.xp;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;
import phoupraw.mcmod.xp_obelisk.misc.XOUtils;
import phoupraw.mcmod.xp_obelisk.mixin.AAbstractFurnaceBlockEntity;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class FurnaceExpStorage extends SnapshotParticipant<Boolean> implements ExpStorage, ExtractionOnlyStorage<ExpSingleton> {
    public final AbstractFurnaceBlockEntity furnace;
    public final NavigableMap<Float, MutablePair<Identifier, Integer>> recipeStats = new TreeMap<>(Comparator.reverseOrder());
    protected long amount = -1;
    public FurnaceExpStorage(AbstractFurnaceBlockEntity furnace) {
        this.furnace = furnace;
        World world = furnace.getWorld();
        if (world == null) return;
        for (Object2IntMap.Entry<Identifier> entry : getFurnace().getRecipesUsed().object2IntEntrySet()) {
            Identifier id = entry.getKey();
            Optional<? extends Recipe<?>> recipeEntry0 = world.getRecipeManager().get(id);
            if (recipeEntry0.isPresent() && recipeEntry0.get() instanceof AbstractCookingRecipe recipe) {
                recipeStats.put(recipe.getExperience(), MutablePair.of(id, entry.getIntValue()));
            }
        }
    }
    @Override
    public long extract(ExpSingleton resource, long maxAmount, TransactionContext transaction) {
        //if (maxAmount < getAmount()) return 0;
        updateSnapshots(transaction);
        double amount = 0;
        for (var entry : recipeStats.entrySet()) {
            float exp = entry.getKey();
            var pair = entry.getValue();
            int count = pair.getRight();
            int extractedCount = Math.min(count, (int) Math.floor((maxAmount - amount) / exp));
            pair.setRight(count - extractedCount);
            amount += exp * extractedCount;
        }
        resetAmount();
        World world = getFurnace().getWorld();
        if (world == null) {
            return (long) XOUtils.twoPoint(amount);
        } else {
            return (long) XOUtils.twoPoint(amount, world.getRandom());
        }
    }
    @Override
    public long getAmount() {
        if (amount == -1) {

            //var furnace = (AbstractFurnaceBlockEntity & AAbstractFurnaceBlockEntity) this.furnace;
            //World world = furnace.getWorld();
            //if (world == null) return 0;
            double amount = 0;
            for (var entry : recipeStats.entrySet()) {
                amount += entry.getKey() * entry.getValue().getRight();
            }
            //for (Object2IntMap.Entry<Identifier> entry : furnace.getRecipesUsed().object2IntEntrySet()) {
            //    Optional<RecipeEntry<?>> recipeEntry0 = world.getRecipeManager().get(entry.getKey());
            //    if (recipeEntry0.isPresent() && recipeEntry0.get().value() instanceof AbstractCookingRecipe recipe) {
            //        amount += recipe.getExperience() * entry.getIntValue();
            //    }
            //}
            setAmount((long) amount);
            //setAmount((long) TIUtils.twoPoint(amount, world.getRandom()));
        }
        return amount;
    }
    @Override
    public long getCapacity() {
        return getAmount();
    }
    @Override
    protected Boolean createSnapshot() {
        return getAmount() == 0;
    }
    @Override
    protected void readSnapshot(Boolean snapshot) {
        if (snapshot) {
            setAmount(0);
        }
    }
    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        var furnace = getFurnace();
        Object2IntOpenHashMap<Identifier> recipesUsed = furnace.getRecipesUsed();
        for (var iterator = recipeStats.entrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            var pair = entry.getValue();
            Identifier id = pair.getLeft();
            int count = pair.getRight();
            if (count == 0) {
                iterator.remove();
                recipesUsed.removeInt(id);
            }else{
                recipesUsed.put(id,count);
            }
        }
        furnace.markDirty();
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }
    public void resetAmount() {
        setAmount(-1);
    }
    @SuppressWarnings("unchecked")
    public <T extends AbstractFurnaceBlockEntity & AAbstractFurnaceBlockEntity> T getFurnace() {
        return (T) furnace;
    }
}
