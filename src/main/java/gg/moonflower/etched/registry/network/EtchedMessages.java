package gg.moonflower.etched.registry.network;

import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.network.play.*;
import gg.moonflower.etched.registry.network.play.handler.EtchedClientPlayPacketHandler;
import gg.moonflower.etched.registry.network.play.handler.EtchedServerPlayPacketHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class EtchedMessages {

    public static void register() {
        PayloadTypeRegistry<RegistryFriendlyByteBuf> S2CRegistry = PayloadTypeRegistry.playS2C();
        PayloadTypeRegistry<RegistryFriendlyByteBuf> C2SRegistry = PayloadTypeRegistry.playC2S();

        // Client
        S2CRegistry.register(ClientboundInvalidEtchUrlPacket.TYPE, ClientboundInvalidEtchUrlPacket.CODEC);
        S2CRegistry.register(ClientboundPlayBlockMusicPacket.TYPE, ClientboundPlayBlockMusicPacket.CODEC);
        S2CRegistry.register(ClientboundPlayEntityMusicPacket.TYPE, ClientboundPlayEntityMusicPacket.CODEC);
        S2CRegistry.register(SetAlbumJukeboxTrackPacket.TYPE, SetAlbumJukeboxTrackPacket.CODEC);
        S2CRegistry.register(SetUrlPacket.TYPE, SetUrlPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundInvalidEtchUrlPacket.TYPE, EtchedClientPlayPacketHandler::handleSetInvalidEtch);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundPlayBlockMusicPacket.TYPE, EtchedClientPlayPacketHandler::handlePlayBlockMusicPacket);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundPlayEntityMusicPacket.TYPE, EtchedClientPlayPacketHandler::handlePlayEntityMusicPacket);
        ClientPlayNetworking.registerGlobalReceiver(SetAlbumJukeboxTrackPacket.TYPE, EtchedClientPlayPacketHandler::handleSetAlbumJukeboxTrack);
        ClientPlayNetworking.registerGlobalReceiver(SetUrlPacket.TYPE, EtchedClientPlayPacketHandler::handleSetUrl);

        // Server
        C2SRegistry.register(ServerboundEditMusicLabelPacket.TYPE, ServerboundEditMusicLabelPacket.CODEC);
        C2SRegistry.register(SetAlbumJukeboxTrackPacket.TYPE, SetAlbumJukeboxTrackPacket.CODEC);
        C2SRegistry.register(SetUrlPacket.TYPE, SetUrlPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundEditMusicLabelPacket.TYPE, EtchedServerPlayPacketHandler::handleEditMusicLabel);
        ServerPlayNetworking.registerGlobalReceiver(SetAlbumJukeboxTrackPacket.TYPE, EtchedServerPlayPacketHandler::handleSetAlbumJukeboxTrack);
        ServerPlayNetworking.registerGlobalReceiver(SetUrlPacket.TYPE, EtchedServerPlayPacketHandler::handleSetUrl);
    }
}
