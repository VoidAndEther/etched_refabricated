package gg.moonflower.etched.registry.component;

import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.Etched;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.function.UnaryOperator;

public class EtchedComponents {

    public static final DataComponentType<TrackData> ALBUM = register(
            "album", builder -> builder
                    .persistent(TrackData.CODEC)
                    .networkSynchronized(TrackData.STREAM_CODEC)
                    .cacheEncoding());
    public static final DataComponentType<AlbumCoverComponent> ALBUM_COVER = register(
            "album_cover", builder -> builder
                    .persistent(AlbumCoverComponent.CODEC)
                    .networkSynchronized(AlbumCoverComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final DataComponentType<DiscAppearanceComponent> DISC_APPEARANCE = register(
            "disc_appearance", builder -> builder
                    .persistent(DiscAppearanceComponent.CODEC)
                    .networkSynchronized(DiscAppearanceComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final DataComponentType<MusicTrackComponent> MUSIC = register(
            "music", builder -> builder
                    .persistent(MusicTrackComponent.CODEC)
                    .networkSynchronized(MusicTrackComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final DataComponentType<MusicLabelComponent> MUSIC_LABEL = register(
            "music_label", builder -> builder
                    .persistent(MusicLabelComponent.CODEC)
                    .networkSynchronized(MusicLabelComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final DataComponentType<PausedComponent> PAUSED = register(
            "paused", builder -> builder
                    .persistent(PausedComponent.CODEC)
                    .networkSynchronized(PausedComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final DataComponentType<PlayingRecordComponent> PLAYING_RECORD = register(
            "playing_record", builder -> builder
                    .persistent(PlayingRecordComponent.CODEC)
                    .networkSynchronized(PlayingRecordComponent.STREAM_CODEC)
                    .cacheEncoding());

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> map) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Etched.id(name), map.apply(DataComponentType.builder()).build());
    }
    public static void register() {
    }
}
