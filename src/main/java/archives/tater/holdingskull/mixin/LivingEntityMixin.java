package archives.tater.holdingskull.mixin;

import archives.tater.holdingskull.SkullGrave;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Definition(id = "ItemEntity", type = ItemEntity.class)
    @Expression("? instanceof ItemEntity")
    @WrapOperation(
            method = "take",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean allowSkullGravePickup(Object object, Operation<Boolean> original) {
        return original.call(object) || object instanceof SkullGrave;
    }
}
