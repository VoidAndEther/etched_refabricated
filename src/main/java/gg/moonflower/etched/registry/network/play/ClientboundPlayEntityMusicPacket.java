package gg.moonflower.etched.registry.network.play;

import gg.moonflower.etched.Etched;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public record ClientboundPlayEntityMusicPacket(Action action, ItemStack record, int entityId) implements CustomPacketPayload {

    public static final Type<ClientboundPlayEntityMusicPacket> TYPE = new Type<>(Etched.id("play_entity_music"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayEntityMusicPacket> CODEC = StreamCodec.of((buf, packet) -> {
        buf.writeEnum(packet.action);
        if (packet.action != Action.STOP) {
            ItemStack.STREAM_CODEC.encode(buf, packet.record);
        }
        buf.writeVarInt(packet.entityId);
    }, buf -> {
        Action action = buf.readEnum(Action.class);
        ItemStack record = action == Action.STOP ? ItemStack.EMPTY : ItemStack.STREAM_CODEC.decode(buf);
        int entityId = buf.readVarInt();
        return new ClientboundPlayEntityMusicPacket(action, record, entityId);
    });

    public ClientboundPlayEntityMusicPacket(ItemStack record, Entity entity, boolean restart) {
        this(restart ? Action.RESTART : Action.START, record, entity.getId());
    }

    public ClientboundPlayEntityMusicPacket(Entity entity) {
        this(Action.STOP, ItemStack.EMPTY, entity.getId());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * @author Ocelot
     */
    public enum Action {
        START, STOP, RESTART
    }
}
