package gg.moonflower.etched.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import gg.moonflower.etched.registry.entity.WorkAtNoteBlock;
import gg.moonflower.etched.registry.profession.EtchedVillagerProfessions;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VillagerGoalPackages.class)
public class VillagerGoalPackagesMixin {
    @ModifyVariable(method = "getWorkPackage", at = @At(value = "STORE"))
    private static WorkAtPoi replaceWorkAtPoi(WorkAtPoi value, @Local(argsOnly = true) VillagerProfession profession) {
        if (profession == EtchedVillagerProfessions.BARD) {
            return new WorkAtNoteBlock();
        }
        return value;
    }
}
