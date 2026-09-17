package io.github.kobaltromero.wattz.registry;

import com.mojang.serialization.MapCodec;
import io.github.kobaltromero.wattz.registry.condition.RecipeEnabled;
import io.github.kobaltromero.wattz.Wattz;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class WattzConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Wattz.MODID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<RecipeEnabled>> RECIPE_ENABLED =
            CONDITIONS.register("recipe_enabled", () -> RecipeEnabled.CODEC);
}
