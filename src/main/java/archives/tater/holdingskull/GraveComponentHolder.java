package archives.tater.holdingskull;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface GraveComponentHolder {

    @Nullable <T extends GraveComponent> T get(GraveComponentType<T> type);
    @Nullable <T extends GraveComponent> T set(GraveComponentType<T> type, T value);
    @Nullable <T extends GraveComponent> T remove(GraveComponentType<T> type);
    <T extends GraveComponent> boolean contains(GraveComponentType<T> type);

    default <T extends GraveComponent> T getOrDefault(GraveComponentType<T> type) {
        return Objects.requireNonNullElse(get(type), type.factory().get());
    }
    default <T extends GraveComponent> T setOrDefault(GraveComponentType<T> type, T value) {
        return Objects.requireNonNullElse(set(type, value), type.factory().get());
    }
}
