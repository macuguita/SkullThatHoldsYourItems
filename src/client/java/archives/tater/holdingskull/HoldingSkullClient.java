package archives.tater.holdingskull;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class HoldingSkullClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        EntityRenderers.register(HoldingSkull.SKULL_GRAVE, SkullGraveEntityRenderer::new);
    }
}