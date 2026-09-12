package io.github.kobaltromero.wattz.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class WattzScenes {
    public static void alternator(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("alternator", "wattz.ponder.alternator.header");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        BlockPos generator = util.grid().at(3, 1, 2);

        scene.idle(5);
        scene.world().showSection(util.select().fromTo(3, 1, 4, 3, 2, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(4, 1, 4), Direction.EAST);
        scene.idle(5);
        scene.world().showSection(util.select().position(4, 2, 4), Direction.DOWN);
        scene.idle(15);
        scene.world().showSection(util.select().position(4, 2, 3), Direction.EAST);
        scene.idle(10);
        scene.world().showSection(util.select().position(3, 1, 3), Direction.EAST);
        scene.idle(10);
        scene.world().showSection(util.select().position(generator), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(3, 1, 1), Direction.SOUTH);

        scene.idle(15);
        scene.overlay()
                .showText(50)
                .text("wattz.ponder.alternator.text_1")
                .placeNearTarget()
                .pointAt(util.vector().topOf(generator));
        scene.idle(60);
        scene.overlay()
                .showText(50)
                .text("wattz.ponder.alternator.text_2")
                .placeNearTarget()
                .pointAt(util.vector().topOf(generator));
        scene.idle(60);
        scene.overlay()
                .showText(50)
                .text("wattz.ponder.alternator.text_3")
                .placeNearTarget()
                .pointAt(util.vector().topOf(generator));
        scene.idle(60);
        scene.overlay()
                .showText(50)
                .text("wattz.ponder.alternator.text_4")
                .placeNearTarget()
                .pointAt(util.vector().topOf(generator));
        scene.idle(60);
        scene.markAsFinished();
    }
}
