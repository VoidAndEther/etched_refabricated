package gg.moonflower.etched.registry.entity;

import gg.moonflower.etched.Etched;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.UnaryOperator;

public class EtchedEntities {
    public static final EntityType<MinecartJukebox> JUKEBOX_MINECART = register("jukebox_minecart", MinecartJukebox::new, MobCategory.MISC, builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8));
    public static <T extends Entity> EntityType<T> register(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> unary) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, Etched.id(name),unary.apply(EntityType.Builder.of(factory, category)).build(name));
    }

    public static void register() {
    }
}
