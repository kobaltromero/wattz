package io.github.kobaltromero.wattz.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class WattzPonder implements PonderPlugin {
    @Override
    public String getModId() {
        return "wattz";
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.addStoryBoard(ResourceLocation.parse("wattz:alternator/crude"), "alternator", WattzScenes::alternator,
                AllCreatePonderTags.KINETIC_APPLIANCES);
        helper.addStoryBoard(ResourceLocation.parse("wattz:alternator/mk1"), "alternator", WattzScenes::alternator,
                AllCreatePonderTags.KINETIC_APPLIANCES);
        helper.addStoryBoard(ResourceLocation.parse("wattz:alternator/mk2"), "alternator", WattzScenes::alternator,
                AllCreatePonderTags.KINETIC_APPLIANCES);
        helper.addStoryBoard(ResourceLocation.parse("wattz:alternator/mk3"), "alternator", WattzScenes::alternator,
                AllCreatePonderTags.KINETIC_APPLIANCES);
        helper.addStoryBoard(ResourceLocation.parse("wattz:alternator/mk4"), "alternator", WattzScenes::alternator,
                AllCreatePonderTags.KINETIC_APPLIANCES);
        helper.addStoryBoard(ResourceLocation.parse("wattz:alternator/mk5"), "alternator", WattzScenes::alternator,
                AllCreatePonderTags.KINETIC_APPLIANCES);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add(ResourceLocation.parse("wattz:alternator/crude"));
        helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add(ResourceLocation.parse("wattz:alternator/mk1"));
        helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add(ResourceLocation.parse("wattz:alternator/mk2"));
        helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add(ResourceLocation.parse("wattz:alternator/mk3"));
        helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add(ResourceLocation.parse("wattz:alternator/mk4"));
        helper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add(ResourceLocation.parse("wattz:alternator/mk5"));
    }
}
