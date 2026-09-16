package io.github.kobaltromero.wattz.tier;

import java.util.List;

import io.github.kobaltromero.wattz.Config;

public class Tier {
    private static final String[] ROMAN_SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    private static final int[] ROMAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    public static String toRoman(int number) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ROMAN_VALUES.length; i++) {
            while (number >= ROMAN_VALUES[i]) {
                number -= ROMAN_VALUES[i];
                sb.append(ROMAN_SYMBOLS[i]);
            }
        }
        return sb.toString();
    }

    public record Alternator(String id) {
        public static final List<Alternator> ALL = TierData.Alternator.DEFAULTS.stream()
                .map(def -> new Alternator(def.id()))
                .toList();

        public String registryName() {
            return "alternator/" + id;
        }

        public int tierNumber() {
            return Integer.parseInt(id.substring(2));
        }

        public String tierNumeral() {
            return toRoman(tierNumber());
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
}
