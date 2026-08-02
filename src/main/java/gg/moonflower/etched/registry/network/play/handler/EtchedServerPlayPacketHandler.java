package gg.moonflower.etched.registry.network.play.handler;

import gg.moonflower.etched.registry.component.MusicLabelComponent;
import gg.moonflower.etched.registry.menu.AlbumJukeboxMenu;
import gg.moonflower.etched.registry.menu.UrlMenu;
import gg.moonflower.etched.registry.network.play.ServerboundEditMusicLabelPacket;
import gg.moonflower.etched.registry.network.play.SetAlbumJukeboxTrackPacket;
import gg.moonflower.etched.registry.network.play.SetUrlPacket;
import gg.moonflower.etched.registry.component.EtchedComponents;
import gg.moonflower.etched.registry.item.EtchedItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EtchedServerPlayPacketHandler {

    public static void handleSetUrl(SetUrlPacket pkt, ServerPlayNetworking.Context ctx) {
        Player player = ctx.player();
        if (player.containerMenu instanceof UrlMenu menu) {
            menu.setUrl(pkt.url());
        }
    }

    public static void handleEditMusicLabel(ServerboundEditMusicLabelPacket pkt, ServerPlayNetworking.Context ctx) {
        int slot = pkt.slot();
        if (!Inventory.isHotbarSlot(slot) && slot != 40) {
            return;
        }

        Player player = ctx.player();
        ItemStack labelStack = player.getInventory().getItem(slot);
        if (!labelStack.is(EtchedItems.MUSIC_LABEL)) {
            return;
        }

        labelStack.update(EtchedComponents.MUSIC_LABEL,
                MusicLabelComponent.DEFAULT,
                label -> label.withInfo(StringUtils.normalizeSpace(pkt.artist()), StringUtils.normalizeSpace(pkt.title())));
    }

    public static void handleSetAlbumJukeboxTrack(SetAlbumJukeboxTrackPacket pkt, ServerPlayNetworking.Context ctx) {
        if (ctx.player() instanceof ServerPlayer sender && sender.containerMenu instanceof AlbumJukeboxMenu menu) {
            ServerLevel level = sender.serverLevel();
            if (menu.setPlayingTrack(level, pkt)) {
                for(ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(new ChunkPos(menu.getPos()), false)) {
                    ServerPlayNetworking.send(player,pkt);
                }
            }
        }
    }
}
