package io.github.kobaltromero.wattz.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import io.github.kobaltromero.wattz.Wattz;
import io.github.kobaltromero.wattz.tier.AlternatorTier;

public class WattzItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wattz.MODID);

    private static final Map<String, DeferredItem<BlockItem>> ALTERNATORS = new LinkedHashMap<>();

    static {
        for (AlternatorTier tier : AlternatorTier.ALL) {
            ALTERNATORS.put(tier.id(), ITEMS.register("alternator/" + tier.id(), () -> new BlockItem(WattzBlocks.getAlternator(tier.id()).get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(Component.literal("Voltage: ")
                            .withStyle(ChatFormatting.DARK_GRAY)
                            .append(Component.literal(tier.getVoltage() + "V").withStyle(ChatFormatting.GRAY)));
                }
            }));
        }
    }

    public static DeferredItem<BlockItem> getAlternator(String tierId) {
        return ALTERNATORS.get(tierId);
    }
}
