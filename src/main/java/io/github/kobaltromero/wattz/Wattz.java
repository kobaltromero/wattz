package io.github.kobaltromero.wattz;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import io.github.kobaltromero.wattz.tier.AlternatorTier;
import io.github.kobaltromero.wattz.registry.WattzBE;
import io.github.kobaltromero.wattz.registry.WattzBlocks;
import io.github.kobaltromero.wattz.registry.WattzItems;

@Mod(Wattz.MODID)
public class Wattz {
    public static final String MODID = "wattz";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Wattz(IEventBus modEventBus, ModContainer modContainer) {
        WattzBlocks.BLOCKS.register(modEventBus);
        WattzItems.ITEMS.register(modEventBus);
        WattzBE.BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addCreativeTabItems);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        for (AlternatorTier tier : AlternatorTier.ALL) {
            Block block = WattzBlocks.getAlternator(tier.id()).get();
            BlockStressValues.IMPACTS.register(block, () -> tier.getMaxStress() / 256.0);

            Item item = WattzItems.getAlternator(tier.id()).get();
            TooltipModifier.REGISTRY.register(item, KineticStats.create(item));
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        WattzBE.registerCapabilities(event);
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            for (AlternatorTier tier : AlternatorTier.ALL) {
                event.accept(WattzItems.getAlternator(tier.id()));
            }
        }
    }
}
