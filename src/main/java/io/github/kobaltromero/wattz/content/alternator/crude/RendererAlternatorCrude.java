package io.github.kobaltromero.wattz.content.alternator.crude;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class RendererAlternatorCrude extends KineticBlockEntityRenderer<BEAlternatorCrude> {

    public RendererAlternatorCrude(Context dispatcher) {
        super(dispatcher);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(BEAlternatorCrude be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state);
    }
}
