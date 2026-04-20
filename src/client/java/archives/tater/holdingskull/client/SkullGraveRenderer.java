package archives.tater.holdingskull.client;

import archives.tater.holdingskull.HoldingSkull;
import archives.tater.holdingskull.SkullGrave;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.Function;

public class SkullGraveRenderer extends EntityRenderer<SkullGrave, SkullGraveRenderState> {
    private final Function<SkullBlock.Type, SkullModelBase> modelByType;
    private final PlayerSkinRenderCache playerSkinRenderCache;

    @SuppressWarnings("DataFlowIssue")
    protected SkullGraveRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        playerSkinRenderCache = ctx.getPlayerSkinRenderCache();
        EntityModelSet modelSet = ctx.getModelSet();
        this.modelByType = Util.memoize(type -> SkullBlockRenderer.createModel(modelSet, type));
        this.shadowRadius = 0.35F;
        this.shadowStrength = 0.75F;
    }

    @Override
    public SkullGraveRenderState createRenderState() {
        return new SkullGraveRenderState();
    }

    @Override
    public void extractRenderState(SkullGrave entity, SkullGraveRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.skullType = entity.getOwner() != null && (!entity.isUnclaimed() || !HoldingSkull.CONFIG.shouldDecayToSkeleton)
                ? SkullBlock.Types.PLAYER
                : SkullBlock.Types.SKELETON;
        state.renderType = resolveSkullRenderType(state.skullType, entity);
    }

    @Override
    public void submit(SkullGraveRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();

        var animationProgress = state.ageInTicks;
        poseStack.translate(0.0F, Mth.sin((animationProgress) / 10f) * 0.1f + 0.1f, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((animationProgress) / 20f));

        poseStack.scale(0.75f, 0.75f, 0.75f);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        SkullModelBase model = this.modelByType.apply(state.skullType);
        if (state.renderType != null)
            SkullBlockRenderer.submitSkull(0.0f, poseStack, submitNodeCollector, state.lightCoords, model, state.renderType, state.outlineColor, null);

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private RenderType resolveSkullRenderType(final SkullBlock.Type type, final SkullGrave entity) {
        if (type == SkullBlock.Types.PLAYER) {
            Player owner = entity.getOwner();
            if (owner != null) {
                ResolvableProfile ownerProfile = entity.getOwner().getProfile();
                return this.playerSkinRenderCache.getOrDefault(ownerProfile).renderType();
            }
        }

        return RenderTypes.entityCutoutZOffset(SkullBlockRenderer.SKIN_BY_TYPE.get(type));
    }
}
