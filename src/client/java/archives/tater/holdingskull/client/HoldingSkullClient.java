package archives.tater.holdingskull.client;

import archives.tater.holdingskull.HoldingSkull;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class HoldingSkullClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        EntityRenderers.register(HoldingSkull.SKULL_GRAVE, SkullGraveRenderer::new);
        MenuScreens.register(HoldingSkull.SKULL_GRAVE_MENU, ContainerScreen::new);
    }
}