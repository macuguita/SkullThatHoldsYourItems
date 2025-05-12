package archives.tater.holdingskull;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SkullGraveEntityRenderer extends EntityRenderer<SkullGraveEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/skeleton.png");

    private final SkullEntityModel model;

    protected SkullGraveEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new SkullEntityModel(ctx.getModelLoader().getModelPart(EntityModelLayers.SKELETON_SKULL));
    }

    @Override
    public void render(SkullGraveEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        var animationProgress = entity.age + tickDelta;
        matrices.translate(0.0F, MathHelper.sin((animationProgress) / 10f) * 0.1f + 0.1f, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((animationProgress) / 20f));

        matrices.scale(0.75f, 0.75f, 0.75f);
        matrices.translate(-0.5f, 0, -0.5f);

        SkullBlockEntityRenderer.renderSkull(null, yaw, animationProgress, matrices, vertexConsumers, light, model, SkullBlockEntityRenderer.getRenderLayer(SkullBlock.Type.SKELETON, null));

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SkullGraveEntity entity) {
        return TEXTURE;
    }
}
