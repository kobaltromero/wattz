package io.github.kobaltromero.wattz.tier;

import java.util.List;

public class TierData {
    public record Alternator(String id, double defaultMaxStress, double defaultVoltage, double defaultMaxAmperage) {

        public static final List<Alternator> DEFAULTS = List.of(
                new Alternator("mk1", 4096, 120.0, 1.0),
                new Alternator("mk2", 8192, 240.0, 2.0),
                new Alternator("mk3", 16384, 480.0, 4.0),
                new Alternator("mk4", 32768, 960.0, 8.0),
                new Alternator("mk5", 65536, 1920.0, 16.0)
        );
    }
}

