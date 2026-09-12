package io.github.kobaltromero.wattz.registry;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import voltaic.registers.VoltaicCapabilities;

import io.github.kobaltromero.wattz.Wattz;
import io.github.kobaltromero.wattz.content.alternator.AlternatorBlockEntity;
import io.github.kobaltromero.wattz.tier.AlternatorTier;

public class WattzBE {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Wattz.MODID);

    private static final Map<String, DeferredHolder<BlockEntityType<?>, BlockEntityType<AlternatorBlockEntity>>> ALTERNATORS = new LinkedHashMap<>();

    static {
        for (AlternatorTier tier : AlternatorTier.ALL) {
            ALTERNATORS.put(tier.id(), BLOCK_ENTITIES.register(tier.registryName(), () -> BlockEntityType.Builder
                    .of((pos, state) -> new AlternatorBlockEntity(getAlternator(tier.id()).get(), pos, state, tier),
                            WattzBlocks.getAlternator(tier.id()).get())
                    .build(null)));
        }
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<AlternatorBlockEntity>> getAlternator(String tierId) {
        return ALTERNATORS.get(tierId);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (DeferredHolder<BlockEntityType<?>, BlockEntityType<AlternatorBlockEntity>> holder : ALTERNATORS.values()) {
            event.registerBlockEntity(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, holder.get(), (be, side) -> be);
        }
    }
}
