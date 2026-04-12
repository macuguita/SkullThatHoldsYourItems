package archives.tater.holdingskull;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.block.SkullBlock;
import org.jspecify.annotations.Nullable;

public class SkullGraveEntityRenderState extends EntityRenderState {
    public @Nullable RenderType renderType;
    public SkullBlock.Type skullType = SkullBlock.Types.SKELETON;
}
