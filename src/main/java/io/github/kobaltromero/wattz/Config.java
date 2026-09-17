package io.github.kobaltromero.wattz;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.kobaltromero.wattz.tier.TierData;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static ModConfigSpec.DoubleValue MAX_STRESS;
    private static ModConfigSpec.DoubleValue TIER_MAX_STRESS;
    private static ModConfigSpec.DoubleValue FE;
    private static ModConfigSpec.DoubleValue BONUS;
    private static ModConfigSpec.BooleanValue RECIPE_ENABLED;
    private static ModConfigSpec.BooleanValue TIER_RECIPE_ENABLED;
    private static ModConfigSpec.DoubleValue VOLTAGE;
    private static ModConfigSpec.DoubleValue AMPERAGE;

    static {
        BUILDER.push("general");
        BONUS  = BUILDER
                .defineInRange("stator_bonus", 0.125, 0.0, 1.0);
        BUILDER.pop();

        BUILDER.push("alternator");
    }

    public record TierValues(ModConfigSpec.DoubleValue maxStress, ModConfigSpec.DoubleValue voltage, ModConfigSpec.DoubleValue amperage, ModConfigSpec.BooleanValue recipeEnabled) {}

    private static final Map<String, TierValues> TIERS = new LinkedHashMap<>();


    static {

        BUILDER.push("crude");

        MAX_STRESS = BUILDER
                .defineInRange("stress_max", 2048, 0.0, Double.MAX_VALUE);
        FE = BUILDER
                .defineInRange("fe_per_tick", 3.0, 0.0, Double.MAX_VALUE);
        RECIPE_ENABLED = BUILDER
                .comment("Default: true")
                .define("recipe_enabled", true);

        BUILDER.pop();

        for (TierData.Alternator def : TierData.Alternator.DEFAULTS) {
            BUILDER.push(def.id());
            TIER_MAX_STRESS = BUILDER
                    .defineInRange("stress_max", def.defaultMaxStress(), 0.0, Double.MAX_VALUE);
            VOLTAGE = BUILDER
                    .defineInRange("voltage", def.defaultVoltage(), 0.0, Double.MAX_VALUE);
            AMPERAGE = BUILDER
                    .defineInRange("amperage", def.defaultAmperage(), 0.0, Double.MAX_VALUE);
            TIER_RECIPE_ENABLED = BUILDER
                    .comment("Default: true")
                    .define("recipe_enabled", def.recipeEnabled());
            TIERS.put(def.id(), new TierValues(TIER_MAX_STRESS, VOLTAGE, AMPERAGE, TIER_RECIPE_ENABLED));
            BUILDER.pop();
        }
        BUILDER.pop();
    }

    public static TierValues getAlternatorTier(String id) {
        return TIERS.get(id);
    }

    public static double getMaxStress() {
        return MAX_STRESS.get();
    }

    public static double getFePerTick() {
        return FE.get();
    }

    public static double getStatorBonus() {
        return BONUS.get();
    }

    public static boolean isRecipeEnabled() {
        return RECIPE_ENABLED.get();
    }

    public static boolean isTierRecipeEnabled(String tierId) {
        TierValues values = TIERS.get(tierId);
        return values != null && values.recipeEnabled().get();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
