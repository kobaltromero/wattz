package io.github.kobaltromero.wattz;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.kobaltromero.tier.TierData;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("alternator");
    }

    public record TierValues(ModConfigSpec.IntValue maxStress, ModConfigSpec.DoubleValue voltage, ModConfigSpec.DoubleValue amperage) {}

    private static final Map<String, TierValues> TIERS = new LinkedHashMap<>();

    static {
        for (TierData def : TierData.DEFAULTS) {
            BUILDER.push(def.id());
            ModConfigSpec.IntValue maxStress = BUILDER
                    .defineInRange("stress_max", def.defaultMaxStress(), 0, Integer.MAX_VALUE);
            ModConfigSpec.DoubleValue voltage = BUILDER
                    .defineInRange("voltage", def.defaultVoltage(), 0.0, Double.MAX_VALUE);
            ModConfigSpec.DoubleValue amperage = BUILDER
                    .defineInRange("amperage_max", def.defaultAmperage(), 0.0, Double.MAX_VALUE);
            TIERS.put(def.id(), new TierValues(maxStress, voltage, amperage));
            BUILDER.pop();
        }
        BUILDER.pop();
    }

    public static TierValues alternatorTier(String id) {
        return TIERS.get(id);
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
