package io.github.kobaltromero.wattz.registry;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.kobaltromero.wattz.tier.Tier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import voltaic.registers.VoltaicCapabilities;

import io.github.kobaltromero.wattz.Wattz;
import io.github.kobaltromero.wattz.content.alternator.crude.BEAlternatorCrude;
import io.github.kobaltromero.wattz.content.alternator.tiered.BEAlternator;

public class WattzBE {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Wattz.MODID);

    private static final Map<String, DeferredHolder<BlockEntityType<?>, BlockEntityType<BEAlternator>>> ALTERNATORS = new LinkedHashMap<>();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BEAlternatorCrude>> CRUDE_ALTERNATOR = BLOCK_ENTITIES.register(
            "alternator/crude",
            () -> BlockEntityType.Builder
                    .of((pos, state) -> new BEAlternatorCrude(getCrudeAlternator().get(), pos, state),
                            WattzBlocks.CRUDE_ALTERNATOR.get(),
                            WattzBlocks.getCrudeAlternatorEncased(WattzBlocks.CasingType.ANDESITE).get(),
                            WattzBlocks.getCrudeAlternatorEncased(WattzBlocks.CasingType.BRASS).get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<BEAlternatorCrude>> getCrudeAlternator() {
        return CRUDE_ALTERNATOR;
    }

    static {
        for (Tier.Alternator tier : Tier.Alternator.ALL) {
            ALTERNATORS.put(tier.id(), BLOCK_ENTITIES.register(tier.registryName(), () -> BlockEntityType.Builder
                    .of((pos, state) -> new BEAlternator(getAlternator(tier.id()).get(), pos, state, tier),
                            WattzBlocks.getAlternator(tier.id()).get(),
                            WattzBlocks.getAlternatorEncased(tier.id(), WattzBlocks.CasingType.ANDESITE).get(),
                            WattzBlocks.getAlternatorEncased(tier.id(), WattzBlocks.CasingType.BRASS).get())
                    .build(null)));
        }
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<BEAlternator>> getAlternator(String tierId) {
        return ALTERNATORS.get(tierId);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (DeferredHolder<BlockEntityType<?>, BlockEntityType<BEAlternator>> holder : ALTERNATORS.values()) {
            event.registerBlockEntity(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, holder.get(),
                    (be, side) -> side == be.getOutputDirection() ? be : null);
        }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, CRUDE_ALTERNATOR.get(),
                (be, side) -> side == be.getOutputDirection() ? be : null);
    }
}
