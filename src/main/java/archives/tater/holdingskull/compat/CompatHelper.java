package archives.tater.holdingskull.compat;

import net.fabricmc.loader.api.FabricLoader;

public final class CompatHelper {

    private CompatHelper() {}

    public static boolean isTrinketsLoaded() {
        return FabricLoader.getInstance().isModLoaded("trinkets_updated");
    }
}
