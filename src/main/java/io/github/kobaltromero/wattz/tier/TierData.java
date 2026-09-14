package io.github.kobaltromero.wattz.tier;

import java.util.List;

public record TierData(String id, double defaultMaxStress, double defaultVoltage, double defaultMaxAmperage) {

    public static final List<TierData> DEFAULTS = List.of(
            new TierData("mk1", 4096, 120.0, 1.0),
            new TierData("mk2", 8192, 240.0, 2.0),
            new TierData("mk3", 16384, 480.0, 4.0),
            new TierData("mk4", 32768, 960.0, 8.0),
            new TierData("mk5", 65536, 1920.0, 16.0)
    );
}
