package phoupraw.mcmod.xp_obelisk.block.entity.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import phoupraw.mcmod.xp_obelisk.block.XpObeliskBlock;
import phoupraw.mcmod.xp_obelisk.block.entity.XpObeliskBlockEntity;
import phoupraw.mcmod.xp_obelisk.transfer.xp.PlayerExpStorage;

import java.util.Objects;

public class StorageBlockEntityRenderer implements BlockEntityRenderer<XpObeliskBlockEntity> {
    public static int getToNextExperienceLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }
    public static Text xp_to_text(int value) {
        int containerLevel = (int) PlayerExpStorage.exp2lvl(value);
        int container_excess_xp = (int) (value - PlayerExpStorage.lvl2exp(containerLevel));
        int container_next_level_xp = getToNextExperienceLevel(containerLevel);
        float container_progress = ((100f / container_next_level_xp) * container_excess_xp);

        String percentage = String.format(java.util.Locale.US, "%.2f", container_progress);

        if (value == 0) {
            return Text.translatable("text.storageBlock.empty");
        }

        if (container_excess_xp == 0) {
            return Text.translatable("text.storageBlock.level", containerLevel);
        }

        return Text.translatable("text.storageBlock.data", containerLevel, percentage);
    }
    public StorageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    @Override
    public void render(XpObeliskBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult rtr = mc.crosshairTarget;

        //System.out.println(entity);
        if (entity != null && entity.getPos() != null && rtr != null && rtr.getType() == HitResult.Type.BLOCK && ((BlockHitResult) rtr).getBlockPos() != null && ((BlockHitResult) rtr).getBlockPos().equals(entity.getPos())) {
            Text levelsString = xp_to_text(entity.getContainerExperience());
            //TranslatableText levelsString = XpFunctions.xp_to_text(Integer.MAX_VALUE);
            float opacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
            int j = (int) (opacity * 255.0F) << 24;
            float halfWidth = -mc.textRenderer.getWidth(levelsString) >> 1;
            Matrix4f positionMatrix;

            matrices.push();
            matrices.translate(0.5D, 1.2D, 0.5D);
            matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getRotation());
            matrices.scale(-0.0125F, -0.0125F, 0.0125F);
            positionMatrix = matrices.peek().getPositionMatrix();

            mc.textRenderer.draw(levelsString, halfWidth, 0, 553648127, false, positionMatrix, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, j, light); //Shadow
            mc.textRenderer.draw(levelsString, halfWidth, 0, -1, false, positionMatrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light); //String
            matrices.pop();
        }

        try {
            //if (entity.containerExperience != 0) {
            ItemStack displayItem;
            if (entity != null && entity.vacuum) {

                displayItem = new ItemStack(Items.HOPPER, 1);
            } else {
                displayItem = new ItemStack(Items.ENCHANTED_BOOK, 1);
            }

            if (entity != null && (entity.getCachedState().get(XpObeliskBlock.CHARGED) || entity.vacuum)) {
                matrices.push();
                long time = Objects.requireNonNull(entity.getWorld()).getTime();

                double offset = Math.sin((time + tickDelta) / 20.0) / 10.0;
                matrices.translate(0.5, 0.40 + offset, 0.5);

                //Quaternionf quaternionf = (new Quaternionf()).rotateY((time + tickDelta) * 3);
                //matrices.multiply(quaternionf);

                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((time + tickDelta) * 3));

                int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
                MinecraftClient.getInstance().getItemRenderer().renderItem(displayItem, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
                matrices.pop();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
            // System.out.println(e);
        }
    }
}
