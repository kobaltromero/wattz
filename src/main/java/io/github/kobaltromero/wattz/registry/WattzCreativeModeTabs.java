package io.github.kobaltromero.wattz.registry;

import io.github.kobaltromero.wattz.Wattz;
import io.github.kobaltromero.wattz.tier.Tier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class WattzCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wattz.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS = TABS.register("wattz/blocks", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(WattzBlocks.getAlternator("mk5")))
            .title(Component.translatable("creative.wattz.blocks"))
            .displayItems((displayParameters, output) -> {
                output.accept(WattzBlocks.CRUDE_ALTERNATOR.get());
                for (Tier.Alternator tier : Tier.Alternator.ALL) {
                    output.acceptAll(List.of(WattzItems.getAlternator(tier.id()).toStack()));
                }
            }).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENCASED = TABS.register("wattz/blocks/encased", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(WattzBlocks.getAlternatorEncased("mk5", WattzBlocks.CasingType.BRASS)))
            .title(Component.translatable("creative.wattz.blocks.encased"))
            .displayItems((displayParameters, output) -> {
                for (WattzBlocks.CasingType casing : WattzBlocks.CasingType.values()) {
                    output.acceptAll(List.of(WattzItems.getCrudeAlternatorEncased(casing).toStack()));
                }
                for (Tier.Alternator tier : Tier.Alternator.ALL) {
                    for (WattzBlocks.CasingType casing : WattzBlocks.CasingType.values()) {
                        output.acceptAll(List.of(WattzItems.getAlternatorEncased(tier.id(), casing).toStack()));
                    }
                }
            }).build());
}
