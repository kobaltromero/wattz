package io.github.kobaltromero.wattz.content.alternator.crude;

import io.github.kobaltromero.wattz.Config;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;


import java.util.List;

public class BEAlternatorCrude extends KineticBlockEntity implements IEnergyStorage {

    private BlockCapabilityCache<IEnergyStorage, Direction> outputCache;
    private boolean firstTickState = true;
    private double energyRemainder = 0.0;

    public BEAlternatorCrude(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public double getProductionRate() {
        return Config.crudeMaxFE() * getRpmFraction();
    }

    public double getProducedPerSecond() {
        return getProductionRate() * 20.0;
    }

    public Direction getOutputDirection() {
        return getBlockState().getValue(DirectionalKineticBlock.FACING).getOpposite();
    }

    public double getRpmFraction() {
        return isSpeedRequirementFulfilled() ? Math.min(1.0, Math.abs(getSpeed()) / 256.0) : 0.0;
    }

    public double getSatisfaction() {
        return getRpmFraction() * 100.0;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        new LangBuilder("wattz")
                .add(Component.translatable("block.wattz.tooltip.speed").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.literal(" " + format(Math.abs(getSpeed())) + "rpm").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.translatable("block.wattz.tooltip.satisfaction").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.literal(" " + String.format("%.1f", getSatisfaction()) + "%").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        double producedPerSecond = getProducedPerSecond();
        new LangBuilder("wattz")
                .add(Component.translatable("block.wattz.tooltip.energy.output").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.literal(" " + format(producedPerSecond) + "FE/s").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        return true;
    }

    @Override
    public float calculateStressApplied() {
        double maxStress = Config.crudeMaxStress();
        double scaledImpact = maxStress / 256.0;

        double speed = Math.abs(getSpeed());
        if (speed > 0.0) {
            scaledImpact = Math.min(scaledImpact, maxStress / speed);
        }

        float impact = (float) scaledImpact;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    protected Block getStressConfigKey() {
        return getBlockState().getBlock();
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && !level.isClientSide()) {
            if (firstTickState) {
                firstTick();
            }
            firstTickState = false;

            if (Math.abs(getSpeed()) > 0.0F && isSpeedRequirementFulfilled()) {
                energyRemainder += getProductionRate();
                int toSend = (int) energyRemainder;

                if (toSend > 0) {
                    IEnergyStorage neighbor = outputCache.getCapability();
                    if (neighbor != null && neighbor.canReceive()) {
                        energyRemainder -= neighbor.receiveEnergy(toSend, false);
                    }
                }
            } else {
                energyRemainder = 0.0;
            }
        }
    }

    public void firstTick() {
        updateCache();
    }

    public void updateCache() {
        if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Direction side = getOutputDirection();
            outputCache = BlockCapabilityCache.create(
                    Capabilities.EnergyStorage.BLOCK, serverLevel, getBlockPos().relative(side), side.getOpposite(),
                    () -> !isRemoved(), () -> {});
        }
    }

    private static final String[] UNITS = {"", "k", "M", "G"};

    private static String format(double n) {
        if (n < 1000) {
            double rounded = Math.round(n * 100.0) / 100.0;
            return rounded == Math.rint(rounded) ? String.valueOf((long) rounded) : String.valueOf(rounded);
        }

        int unitIndex = 0;
        while (n >= 1000 && unitIndex < UNITS.length - 1) {
            n /= 1000.0;
            unitIndex++;
        }

        return (Math.round(n * 10.0) / 10.0) + UNITS[unitIndex];
    }

    @Override
    public int receiveEnergy(int i, boolean b) {
        return 0;
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        return (int) getProductionRate();
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}

