package archives.tater.holdingskull.mixin;

import archives.tater.holdingskull.SkullGrave;
import archives.tater.holdingskull.compat.CompatHelper;
import archives.tater.holdingskull.compat.Trinkets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("resource")
@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar implements ContainerUser {

    @Shadow
    protected abstract void destroyVanishingCursedItems();

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "dropEquipment",
            at = @At("HEAD"),
            cancellable = true
    )
    private void holdingskull$createGrave(ServerLevel level, CallbackInfo ci) {
        var keepInv = level.getGameRules().get(GameRules.KEEP_INVENTORY);
        if (keepInv) return;
        this.destroyVanishingCursedItems();
        level().addFreshEntity(new SkullGrave(level, (Player) (Object) this));
        ci.cancel();
    }
}