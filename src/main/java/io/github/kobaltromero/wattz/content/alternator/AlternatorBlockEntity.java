package io.github.kobaltromero.wattz.content.alternator;

import java.util.EnumMap;
import java.util.List;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import io.github.kobaltromero.tier.AlternatorTier;
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

import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.registers.VoltaicCapabilities;

public class AlternatorBlockEntity extends KineticBlockEntity implements ICapabilityElectrodynamic {
    private final EnumMap<Direction, BlockCapabilityCache<ICapabilityElectrodynamic, Direction>> cache = new EnumMap<>(Direction.class);
    private final AlternatorTier tier;
    private boolean firstTickState = true;

    public AlternatorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, AlternatorTier tier) {
        super(typeIn, pos, state);
        this.tier = tier;
    }

    @Override
    public double getJoulesStored() {
        return 0.0;
    }

    @Override
    public double getMaxJoulesStored() {
        return 0.0;
    }

    @Override
    public void setJoulesStored(double joules) {
    }

    @Override
    public double getVoltage() {
        return tier.getVoltage();
    }

    @Override
    public boolean isEnergyReceiver() {
        return false;
    }

    @Override
    public boolean isEnergyProducer() {
        return true;
    }

    @Override
    public void onChange() {
        setChanged();
    }

    @Override
    public TransferPack getConnectedLoad(ICapabilityElectrodynamic.LoadProfile profile, Direction direction) {
        return getProduced();
    }

    public TransferPack getProduced() {
        double amps = tier.getAmperage() * getRpmFraction();
        return TransferPack.ampsVoltage(amps, getVoltage());
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
        TransferPack produced = getProduced();
        new LangBuilder("wattz")
                .add(Component.translatable("block.wattz.tooltip.energy.output").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.literal(" " + format(produced.getWatts()) + "W ").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.translatable("block.wattz.tooltip.energy.voltage").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.literal(" " + format(getVoltage()) + "V").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.translatable("block.wattz.tooltip.energy.current").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        new LangBuilder("wattz")
                .add(Component.literal(" " + format(produced.getAmps()) + "A ").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        return true;
    }

    @Override
    public float calculateStressApplied() {
        float impact = (float) (tier.getMaxStress() / 256.0);
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
                double available = getProduced().getJoules();

                for (Direction d : Direction.values()) {
                    if (available <= 0.0) {
                        break;
                    }
                    ICapabilityElectrodynamic neighbor = cache.get(d).getCapability();
                    if (neighbor == null || !neighbor.isEnergyReceiver()) {
                        continue;
                    }

                    TransferPack result = neighbor.receivePower(TransferPack.joulesVoltage(available, getVoltage()), false);

                    if (neighbor.getVoltage() <= getVoltage() || neighbor.getVoltage() == -1.0) {
                        available -= result.getJoules();
                    }
                }
            }
        }
    }

    public void firstTick() {
        updateCache();
    }

    public void updateCache() {
        if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            for (Direction side : Direction.values()) {
                cache.put(side, BlockCapabilityCache.create(
                        VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, serverLevel, getBlockPos().relative(side), side.getOpposite(),
                        () -> !isRemoved(), () -> {}));
            }
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
}
