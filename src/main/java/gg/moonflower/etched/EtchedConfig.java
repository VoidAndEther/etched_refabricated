package gg.moonflower.etched;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record EtchedConfig(boolean useBoomboxMenu, boolean useAlbumCoverMenu) {
    public static EtchedConfig INSTANCE;
    public static final Codec<EtchedConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("use_boombox_menu", false).forGetter(EtchedConfig::useBoomboxMenu),
            Codec.BOOL.optionalFieldOf("use_album_cover_menu", true).forGetter(EtchedConfig::useAlbumCoverMenu)
    ).apply(instance, EtchedConfig::new));
    public static EtchedConfig deserialize() {
        Path config = FabricLoader.getInstance().getConfigDir().resolve("etched");
        if (Files.exists(config)) {
            try {
                BufferedReader reader = Files.newBufferedReader(config);
                EtchedConfig result = CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(reader)).result()
                        .orElse(new EtchedConfig(false, true));
                reader.close();
                return result;
            } catch (IOException | JsonParseException e) {
                return new EtchedConfig(false, true);
            }
        } else {
            return new EtchedConfig(false, true);
        }
    }
}
