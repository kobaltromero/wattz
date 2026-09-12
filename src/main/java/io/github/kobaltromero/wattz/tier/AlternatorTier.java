package io.github.kobaltromero.wattz.tier;

import java.util.List;

import io.github.kobaltromero.wattz.Config;

public record AlternatorTier(String id) {
    public static final List<AlternatorTier> ALL = TierData.DEFAULTS.stream()
            .map(def -> new AlternatorTier(def.id()))
            .toList();

    public String registryName() {
        return "alternator/" + id;
    }

    public double getVoltage() {
        return Config.alternatorTier(id).voltage().get();
    }

    public double getAmperage() {
        return Config.alternatorTier(id).amperage().get();
    }

    public double getMaxStress() {
        return Config.alternatorTier(id).maxStress().get();
    }
}
