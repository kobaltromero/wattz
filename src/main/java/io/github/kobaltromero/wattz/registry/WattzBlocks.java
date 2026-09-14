package io.github.kobaltromero.wattz.registry;

import java.util.LinkedHashMap;
import java.util.Map;

import com.simibubi.create.foundation.data.SharedProperties;

import io.github.kobaltromero.wattz.tier.Tier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import io.github.kobaltromero.wattz.Wattz;
import io.github.kobaltromero.wattz.content.alternator.AlternatorBlock;

public class WattzBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Wattz.MODID);

    private static final Map<String, DeferredBlock<AlternatorBlock>> ALTERNATORS = new LinkedHashMap<>();

    static {
        for (Tier.Alternator tier : Tier.Alternator.ALL) {
            ALTERNATORS.put(tier.id(), BLOCKS.registerBlock(
                    tier.registryName(),
                    props -> new AlternatorBlock(props, tier, () -> WattzBE.getAlternator(tier.id()).get()),
                    BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal()).requiresCorrectToolForDrops()));
        }
    }

    public static DeferredBlock<AlternatorBlock> getAlternator(String tierId) {
        return ALTERNATORS.get(tierId);
    }
}
