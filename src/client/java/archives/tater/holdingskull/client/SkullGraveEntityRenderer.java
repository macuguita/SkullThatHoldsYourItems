package archives.tater.holdingskull.client;

import archives.tater.holdingskull.SkullGraveEntity;
import com.google.common.collect.Maps;
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
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Map;
import java.util.function.Function;

public class SkullGraveEntityRenderer extends EntityRenderer<SkullGraveEntity, SkullGraveEntityRenderState> {
    private final Function<SkullBlock.Type, SkullModelBase> modelByType;
    private static final Map<SkullBlock.Type, Identifier> SKIN_BY_TYPE = Util.make(Maps.newHashMap(), map -> {
        map.put(SkullBlock.Types.SKELETON, Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png"));
        map.put(SkullBlock.Types.WITHER_SKELETON, Identifier.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png"));
        map.put(SkullBlock.Types.ZOMBIE, Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png"));
        map.put(SkullBlock.Types.CREEPER, Identifier.withDefaultNamespace("textures/entity/creeper/creeper.png"));
        map.put(SkullBlock.Types.DRAGON, Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon.png"));
        map.put(SkullBlock.Types.PIGLIN, Identifier.withDefaultNamespace("textures/entity/piglin/piglin.png"));
        map.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultTexture());
    });
    private final PlayerSkinRenderCache playerSkinRenderCache;

    @SuppressWarnings("DataFlowIssue")
    protected SkullGraveEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        playerSkinRenderCache = ctx.getPlayerSkinRenderCache();
        EntityModelSet modelSet = ctx.getModelSet();
        this.modelByType = Util.memoize(type -> SkullBlockRenderer.createModel(modelSet, type));
        this.shadowRadius = 0.35F;
        this.shadowStrength = 0.75F;
    }

    @Override
    public SkullGraveEntityRenderState createRenderState() {
        return new SkullGraveEntityRenderState();
    }

    @Override
    public void extractRenderState(SkullGraveEntity entity, SkullGraveEntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.skullType = entity.getOwner() != null
                ? SkullBlock.Types.PLAYER
                : SkullBlock.Types.SKELETON;
        state.renderType = resolveSkullRenderType(state.skullType, entity);
    }

    @Override
    public void submit(SkullGraveEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
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

    private RenderType resolveSkullRenderType(final SkullBlock.Type type, final SkullGraveEntity entity) {
        if (type == SkullBlock.Types.PLAYER) {
            Player owner = entity.getOwner();
            if (owner != null) {
                ResolvableProfile ownerProfile = entity.getOwner().getProfile();
                return this.playerSkinRenderCache.getOrDefault(ownerProfile).renderType();
            }
        }

        return RenderTypes.entityCutoutZOffset(SKIN_BY_TYPE.get(type));
    }
}
