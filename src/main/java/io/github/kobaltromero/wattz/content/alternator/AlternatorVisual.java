package io.github.kobaltromero.wattz.content.alternator;

import java.util.function.Consumer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AlternatorVisual extends KineticBlockEntityVisual<AlternatorBlockEntity> {
    protected final RotatingInstance shaft;

    public AlternatorVisual(VisualizationContext context, AlternatorBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        Direction facing = blockState.getValue(BlockStateProperties.FACING);
        Instancer<RotatingInstance> instancer = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF));

        shaft = instancer.createInstance()
                .rotateToFace(Direction.SOUTH, facing)
                .setup(blockEntity)
                .setPosition(getVisualPosition());
        shaft.setChanged();
    }

    @Override
    public void update(float pt) {
        shaft.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(shaft);
    }

    @Override
    protected void _delete() {
        shaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(shaft);
    }
}
