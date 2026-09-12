package io.github.kobaltromero.wattz;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.ponder.foundation.PonderIndex;

import io.github.kobaltromero.wattz.content.alternator.AlternatorBlockEntity;
import io.github.kobaltromero.wattz.content.alternator.AlternatorRenderer;
import io.github.kobaltromero.wattz.tier.AlternatorTier;
import io.github.kobaltromero.wattz.content.alternator.AlternatorVisual;
import io.github.kobaltromero.wattz.ponder.WattzPonder;
import io.github.kobaltromero.wattz.registry.WattzBE;

@Mod(value = Wattz.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Wattz.MODID, value = Dist.CLIENT)
public class WattzClient {
    public WattzClient(IEventBus modEventBus) {
        modEventBus.addListener(this::registerRenderers);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (AlternatorTier tier : AlternatorTier.ALL) {
            BlockEntityType<AlternatorBlockEntity> type = WattzBE.getAlternator(tier.id()).get();

            event.registerBlockEntityRenderer(type, AlternatorRenderer::new);

            VisualizerRegistry.setVisualizer(type,
                    new SimpleBlockEntityVisualizer.Builder<>(type)
                            .factory(AlternatorVisual::new)
                            .apply());
        }
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new WattzPonder());
    }
}
