package archives.tater.holdingskull.mixin;

import archives.tater.holdingskull.SkullGraveEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(
            method = "sendPickup",
            constant = @Constant(classValue = ItemEntity.class)
    )
    private boolean allowSkullGravePickup(Object object, Operation<Boolean> original) {
        return original.call(object) || object instanceof SkullGraveEntity;
    }
}
