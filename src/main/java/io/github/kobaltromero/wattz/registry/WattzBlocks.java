package io.github.kobaltromero.wattz.registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.SharedProperties;

import io.github.kobaltromero.wattz.tier.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import io.github.kobaltromero.wattz.Wattz;
import io.github.kobaltromero.wattz.content.alternator.crude.BlockAlternatorCrude;
import io.github.kobaltromero.wattz.content.alternator.crude.EncasedBlockAlternatorCrude;
import io.github.kobaltromero.wattz.content.alternator.tiered.BlockAlternator;
import io.github.kobaltromero.wattz.content.alternator.tiered.EncasedBlockAlternator;

public class WattzBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Wattz.MODID);

    public enum CasingType {
        ANDESITE("andesite", AllBlocks.ANDESITE_CASING::get),
        COPPER("copper", AllBlocks.COPPER_CASING::get),
        BRASS("brass", AllBlocks.BRASS_CASING::get);

        public final String id;
        public final Supplier<? extends Block> block;

        CasingType(String id, Supplier<? extends Block> block) {
            this.id = id;
            this.block = block;
        }
    }

    private static final Map<String, DeferredBlock<BlockAlternator>> ALTERNATORS = new LinkedHashMap<>();
    private static final Map<String, DeferredBlock<EncasedBlockAlternator>> ALTERNATORS_ENCASED = new LinkedHashMap<>();
    private static final Map<String, DeferredBlock<EncasedBlockAlternatorCrude>> CRUDE_ALTERNATOR_ENCASED = new LinkedHashMap<>();

    public static final DeferredBlock<BlockAlternatorCrude> CRUDE_ALTERNATOR = BLOCKS.registerBlock(
            "alternator/crude",
            props -> new BlockAlternatorCrude(props, WattzBE.CRUDE_ALTERNATOR),
            BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal()).requiresCorrectToolForDrops());

    static {
        for (Tier.Alternator tier : Tier.Alternator.ALL) {
            ALTERNATORS.put(tier.id(), BLOCKS.registerBlock(
                    tier.registryName(),
                    props -> new BlockAlternator(props, tier, () -> WattzBE.getAlternator(tier.id()).get()),
                    BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal()).requiresCorrectToolForDrops()));
        }

        for (CasingType casing : CasingType.values()) {
            for (Tier.Alternator tier : Tier.Alternator.ALL) {
                ALTERNATORS_ENCASED.put(encasedKey(tier.id(), casing), BLOCKS.registerBlock(
                        tier.registryName() + "/encased/" + casing.id,
                        props -> new EncasedBlockAlternator(props, tier, () -> WattzBE.getAlternator(tier.id()).get(),
                                casing.block, () -> WattzBlocks.getAlternator(tier.id()).get()),
                        BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal()).requiresCorrectToolForDrops().noOcclusion()));
            }

            CRUDE_ALTERNATOR_ENCASED.put(casing.id, BLOCKS.registerBlock(
                    "alternator/crude/encased/" + casing.id,
                    props -> new EncasedBlockAlternatorCrude(props, WattzBE.CRUDE_ALTERNATOR::get,
                            casing.block, WattzBlocks.CRUDE_ALTERNATOR),
                    BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal()).requiresCorrectToolForDrops().noOcclusion()));
        }
    }

    public static DeferredBlock<BlockAlternator> getAlternator(String tierId) {
        return ALTERNATORS.get(tierId);
    }

    public static DeferredBlock<EncasedBlockAlternator> getAlternatorEncased(String tierId, CasingType type) {
        return ALTERNATORS_ENCASED.get(encasedKey(tierId, type));
    }

    private static String encasedKey(String tierId, CasingType type) {
        return tierId + "/" + type.id;
    }

    public static DeferredBlock<EncasedBlockAlternatorCrude> getCrudeAlternatorEncased(CasingType casing) {
        return CRUDE_ALTERNATOR_ENCASED.get(casing.id);
    }
}
