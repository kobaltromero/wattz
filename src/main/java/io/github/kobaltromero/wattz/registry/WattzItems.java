package io.github.kobaltromero.wattz.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.kobaltromero.wattz.Config;
import io.github.kobaltromero.wattz.tier.Tier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import io.github.kobaltromero.wattz.Wattz;

public class WattzItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wattz.MODID);

    private static final Map<String, DeferredItem<BlockItem>> ALTERNATORS = new LinkedHashMap<>();
    private static final Map<String, DeferredItem<BlockItem>> ALTERNATORS_ENCASED = new LinkedHashMap<>();
    private static final Map<String, DeferredItem<BlockItem>> CRUDE_ALTERNATOR_ENCASED = new LinkedHashMap<>();

    public static final DeferredItem<BlockItem> CRUDE_ALTERNATOR = ITEMS.register(
            "alternator/crude",
            () -> new BlockItem(WattzBlocks.CRUDE_ALTERNATOR.get(), new Item.Properties()));

    static {
        for (Tier.Alternator tier : Tier.Alternator.ALL) {
            ALTERNATORS.put(tier.id(), ITEMS.register(tier.registryName(), () -> new BlockItem(WattzBlocks.getAlternator(tier.id()).get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(Component.literal("Tier: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal(tier.tierNumeral()).withStyle(ChatFormatting.GRAY)));
                    tooltip.add(Component.literal("Voltage: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal(tier.getVoltage() + "V").withStyle(ChatFormatting.GRAY)));
                }
            }));
        }

        for (WattzBlocks.CasingType casing : WattzBlocks.CasingType.values()) {
            for (Tier.Alternator tier : Tier.Alternator.ALL) {
                ALTERNATORS_ENCASED.put(getKey(tier.id(), casing), ITEMS.register(
                        tier.registryName() + "/encased/" + casing.id,
                        () -> new BlockItem(WattzBlocks.getAlternatorEncased(tier.id(), casing).get(), new Item.Properties()) {
                            @Override
                            public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
                                tooltip.add(Component.literal("Tier: ")
                                        .withStyle(ChatFormatting.DARK_GRAY)
                                        .append(Component.literal(tier.tierNumeral()).withStyle(ChatFormatting.GRAY)));
                                tooltip.add(Component.literal("Voltage: ")
                                        .withStyle(ChatFormatting.DARK_GRAY)
                                        .append(Component.literal(tier.getVoltage() + "V").withStyle(ChatFormatting.GRAY)));
                            }
                        }));
            }

            CRUDE_ALTERNATOR_ENCASED.put(casing.id, ITEMS.register(
                    "alternator/crude/encased/" + casing.id,
                    () -> new BlockItem(WattzBlocks.getCrudeAlternatorEncased(casing).get(), new Item.Properties())));
        }
    }

    public static DeferredItem<BlockItem> getAlternator(String tierId) {
        return ALTERNATORS.get(tierId);
    }

    public static DeferredItem<BlockItem> getAlternatorEncased(String tierId, WattzBlocks.CasingType type) {
        return ALTERNATORS_ENCASED.get(getKey(tierId, type));
    }

    public static DeferredItem<BlockItem> getCrudeAlternatorEncased(WattzBlocks.CasingType casing) {
        return CRUDE_ALTERNATOR_ENCASED.get(casing.id);
    }

    private static String getKey(String tierId, WattzBlocks.CasingType type) {
        return tierId + "/" + type.id;
    }
}
