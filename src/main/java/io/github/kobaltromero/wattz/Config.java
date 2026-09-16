package io.github.kobaltromero.wattz;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.kobaltromero.wattz.tier.TierData;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("general");
        STATOR_BONUS = BUILDER
                .defineInRange("stator_bonus", 0.125, 0.0, 1.0);
        BUILDER.pop();
        BUILDER.push("alternator");
    }

    public record TierValues(ModConfigSpec.DoubleValue maxStress, ModConfigSpec.DoubleValue voltage, ModConfigSpec.DoubleValue amperage) {}

    private static final Map<String, TierValues> TIERS = new LinkedHashMap<>();

    private static final ModConfigSpec.DoubleValue STATOR_BONUS;
    private static final ModConfigSpec.DoubleValue MAX_STRESS;
    private static final ModConfigSpec.DoubleValue MAX_FE_PER_TICK;

    static {

        BUILDER.push("crude");
        MAX_STRESS = BUILDER
                .defineInRange("stress_max", 2048, 0.0, Double.MAX_VALUE);
        MAX_FE_PER_TICK = BUILDER
                .defineInRange("max_fe_per_tick", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

        for (TierData.Alternator def : TierData.Alternator.DEFAULTS) {
            BUILDER.push(def.id());
            ModConfigSpec.DoubleValue maxStress = BUILDER
                    .defineInRange("stress_max", def.defaultMaxStress(), 0.0, Double.MAX_VALUE);
            ModConfigSpec.DoubleValue voltage = BUILDER
                    .defineInRange("voltage", def.defaultVoltage(), 0.0, Double.MAX_VALUE);
            ModConfigSpec.DoubleValue amperage = BUILDER
                    .defineInRange("amperage_max", def.defaultMaxAmperage(), 0.0, Double.MAX_VALUE);
            TIERS.put(def.id(), new TierValues(maxStress, voltage, amperage));
            BUILDER.pop();
        }
        BUILDER.pop();
    }

    public static TierValues alternatorTier(String id) {
        return TIERS.get(id);
    }

    public static double statorBonus() {
        return STATOR_BONUS.get();
    }

    public static double crudeMaxStress() {
        return MAX_STRESS.get();
    }

    public static double crudeMaxFE() {
        return MAX_FE_PER_TICK.get();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
