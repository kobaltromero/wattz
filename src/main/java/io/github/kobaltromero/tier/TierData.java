package io.github.kobaltromero.tier;

import java.util.List;

public record TierData(String id, int defaultMaxStress, double defaultVoltage, double defaultAmperage) {

    public static final List<TierData> DEFAULTS = List.of(
            new TierData("mk1", 4096, 120.0, 0.29166666666),
            new TierData("mk2", 4096, 240.0, 0.29166666666),
            new TierData("mk3", 4096, 480.0, 0.29166666666),
            new TierData("mk4", 4096, 960.0, 0.29166666666),
            new TierData("mk5", 4096, 1920.0, 0.29166666666)
    );
}
