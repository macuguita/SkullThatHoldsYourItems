package archives.tater.holdingskull.client.mixin;

import archives.tater.holdingskull.SkullGraveEntity;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("resource")
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Definition(id = "dropAllDeathLoot", method = "Lnet/minecraft/server/level/ServerPlayer;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V")
    @Expression("this.dropAllDeathLoot(?, ?)")
    @Inject(
            method = "die",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private void createGrave(DamageSource source, CallbackInfo ci) {
        level().addFreshEntity(new SkullGraveEntity(level(), this));
    }
}