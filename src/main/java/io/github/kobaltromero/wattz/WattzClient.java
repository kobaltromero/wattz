package io.github.kobaltromero.wattz;

import io.github.kobaltromero.wattz.content.alternator.crude.BEAlternatorCrude;
import io.github.kobaltromero.wattz.content.alternator.crude.RendererAlternatorCrude;
import io.github.kobaltromero.wattz.content.alternator.crude.VisualAlternatorCrude;
import io.github.kobaltromero.wattz.content.alternator.tiered.BEAlternator;
import io.github.kobaltromero.wattz.tier.Tier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.ponder.foundation.PonderIndex;

import io.github.kobaltromero.wattz.content.alternator.tiered.RendererAlternator;
import io.github.kobaltromero.wattz.content.alternator.tiered.VisualAlternator;
import io.github.kobaltromero.wattz.ponder.WattzPonder;
import io.github.kobaltromero.wattz.registry.WattzBE;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Wattz.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Wattz.MODID, value = Dist.CLIENT)
public class WattzClient {
    public WattzClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::registerRenderers);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (Tier.Alternator tier : Tier.Alternator.ALL) {
            BlockEntityType<BEAlternator> type = WattzBE.getAlternator(tier.id()).get();

            event.registerBlockEntityRenderer(type, RendererAlternator::new);

            VisualizerRegistry.setVisualizer(type,
                    new SimpleBlockEntityVisualizer.Builder<>(type)
                            .factory(VisualAlternator::new)
                            .apply());
        }

        BlockEntityType<BEAlternatorCrude> crudeType = WattzBE.CRUDE_ALTERNATOR.get();

        event.registerBlockEntityRenderer(crudeType, RendererAlternatorCrude::new);

        VisualizerRegistry.setVisualizer(crudeType,
                new SimpleBlockEntityVisualizer.Builder<>(crudeType)
                        .factory(VisualAlternatorCrude::new)
                        .apply());
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new WattzPonder());
    }
}
