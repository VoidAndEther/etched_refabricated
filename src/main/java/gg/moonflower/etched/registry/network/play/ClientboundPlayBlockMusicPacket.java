package gg.moonflower.etched.registry.network.play;

import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.Etched;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * @param record The record to play
 * @param pos    The position the music disk is playing at
 * @author Ocelot
 */
@ApiStatus.Internal
public record ClientboundPlayBlockMusicPacket(ItemStack record, BlockPos pos) implements CustomPacketPayload {

    public static final Type<ClientboundPlayBlockMusicPacket> TYPE = new Type<>(Etched.id("play_block_music"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayBlockMusicPacket> CODEC = StreamCodec.of((buf, packet) -> {
        ItemStack.STREAM_CODEC.encode(buf, packet.record);
        buf.writeBlockPos(packet.pos);
    }, buf -> {
        ItemStack record = ItemStack.STREAM_CODEC.decode(buf);
        BlockPos pos = buf.readBlockPos();
        return new ClientboundPlayBlockMusicPacket(record, pos);
    });

    @Override
    public @NotNull Type<ClientboundPlayBlockMusicPacket> type() {
        return TYPE;
    }

    /**
     * @param registries The registry instance to get data from
     * @return The tracks to play in sequence
     */
    public List<TrackData> tracks(HolderLookup.Provider registries) {
        return PlayableRecord.getTracks(registries, this.record);
    }
}
