package io.github.foundationgames.deathrun.game.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record MapText(Vec3 pos, TextData text) {
    public record TextData(List<Component> lines) {
        public static final Codec<Component> JSON_TEXT_CODEC = ComponentSerialization.CODEC;

        public static final Codec<TextData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(JSON_TEXT_CODEC).fieldOf("lines").forGetter(TextData::lines)
        ).apply(instance, TextData::new));
    }
}
