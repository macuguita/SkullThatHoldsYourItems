package archives.tater.holdingskull;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.block.SkullBlock;

public class SkullGraveEntityRenderState extends EntityRenderState {
    public RenderType renderType;
    public SkullBlock.Type skullType = SkullBlock.Types.SKELETON;
}
