package archives.tater.holdingskull;

import com.mojang.serialization.Codec;

import java.util.function.Supplier;

public record GraveComponentType<T extends GraveComponent>(
        Codec<T> codec,
        Supplier<T> factory
) {}
