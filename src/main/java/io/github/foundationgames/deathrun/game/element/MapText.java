package io.github.foundationgames.deathrun.game.element;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public record MapText(Vec3d pos, TextData text) {
    private static final RegistryWrapper.WrapperLookup LOOKUP = DynamicRegistryManager.of(Registries.REGISTRIES);

    public record TextData(List<Text> lines) {
        public static final Codec<Text> JSON_TEXT_CODEC = new Codec<Text>() {
            @Override
            public <T> DataResult<Pair<Text, T>> decode(DynamicOps<T> ops, T input) {
                var decoded = TextCodecs.CODEC.decode(ops, input);

                if (decoded.isSuccess()) {
                    var val = decoded.getOrThrow().getFirst();
                    if (val.getSiblings().isEmpty() && val.getStyle().isEmpty() && val.getContent() instanceof PlainTextContent.Literal literal) {
                        if (literal.string().length() > 2 && literal.string().charAt(0) == '"' && literal.string().charAt(literal.string().length() - 1) == '"') {
                            return DataResult.success(new Pair<>(Text.literal(literal.string().substring(1, literal.string().length() - 2)), decoded.getOrThrow().getSecond()));
                        } else if (literal.string().length() > 2 && literal.string().charAt(0) == '{' && literal.string().charAt(literal.string().length() - 1) == '}') {
                            try {
                                var json = JsonParser.parseString(literal.string());
                                return DataResult.success(new Pair<>(TextCodecs.CODEC.decode(ops instanceof RegistryOps<T> registryOps ? registryOps.withDelegate(JsonOps.INSTANCE) : JsonOps.INSTANCE, json).getOrThrow().getFirst(), decoded.getOrThrow().getSecond()));
                            } catch (Throwable throwable) {
                                // ignored
                            }

                        }
                    }
                }

                return decoded;
            }

            @Override
            public <T> DataResult<T> encode(Text input, DynamicOps<T> ops, T prefix) {
                return TextCodecs.CODEC.encode(input.copy().setStyle(input.getStyle().withInsertion("")), ops, prefix);
            }
        };

        public static final Codec<TextData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(JSON_TEXT_CODEC).fieldOf("lines").forGetter(TextData::lines)
        ).apply(instance, TextData::new));
    }
}
