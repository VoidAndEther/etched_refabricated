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

public record EtchedClientConfig(boolean forceStereo, boolean smoothParrotAnimation) {
    public static EtchedClientConfig INSTANCE;
    public static final Codec<EtchedClientConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("force_stereo", false).forGetter(EtchedClientConfig::forceStereo),
            Codec.BOOL.optionalFieldOf("smooth_parrot_animation", true).forGetter(EtchedClientConfig::smoothParrotAnimation)
    ).apply(instance, EtchedClientConfig::new));
    public static EtchedClientConfig deserialize() {
        Path config = FabricLoader.getInstance().getConfigDir().resolve("etched_client");
        if (Files.exists(config)) {
            try {
                BufferedReader reader = Files.newBufferedReader(config);
                EtchedClientConfig result = CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(reader)).result()
                        .orElse(new EtchedClientConfig(false, true));
                reader.close();
                return result;
            } catch (IOException | JsonParseException e) {
                return new EtchedClientConfig(false, true);
            }
        } else {
            return new EtchedClientConfig(false, true);
        }
    }
}
