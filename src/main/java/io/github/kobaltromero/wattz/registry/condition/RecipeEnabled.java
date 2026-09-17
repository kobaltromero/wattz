package io.github.kobaltromero.wattz.registry.condition;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.kobaltromero.wattz.Config;
import net.neoforged.neoforge.common.conditions.ICondition;

public record RecipeEnabled(Optional<String> tier) implements ICondition {
    public static final MapCodec<RecipeEnabled> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(
                    Codec.STRING.optionalFieldOf("tier").forGetter(RecipeEnabled::tier))
            .apply(builder, RecipeEnabled::new));

    @Override
    public boolean test(IContext ctx) {
        return tier.map(Config::isTierRecipeEnabled).orElseGet(Config::isRecipeEnabled);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "wattz:recipe_enabled(" + tier.orElse("crude") + ")";
    }
}
