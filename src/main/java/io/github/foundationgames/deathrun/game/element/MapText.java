package io.github.foundationgames.deathrun.game.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public record MapText(Vec3d pos, TextData text) {
    private static  final RegistryWrapper.WrapperLookup LOOKUP = DynamicRegistryManager.of(Registries.REGISTRIES);
    public record TextData(List<Text> lines) {
        public static final Codec<Text> JSON_TEXT_CODEC = Codec.STRING.xmap((x) -> Text.Serialization.fromJson(x, LOOKUP),
                x -> Text.Serialization.toJsonString(x, LOOKUP));

        public static final Codec<TextData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(JSON_TEXT_CODEC).fieldOf("lines").forGetter(TextData::lines)
        ).apply(instance, TextData::new));
    }
}
