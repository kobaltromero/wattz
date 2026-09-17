package io.github.kobaltromero.wattz.content.alternator.tiered;

import java.util.List;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import io.github.kobaltromero.wattz.Config;
import io.github.kobaltromero.wattz.tier.Tier;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.registers.VoltaicCapabilities;

public class BEAlternator extends KineticBlockEntity implements ICapabilityElectrodynamic {
    private static final String UPGRADE_TAG = "stator";

    private BlockCapabilityCache<ICapabilityElectrodynamic, Direction> outputCache;
    private final Tier.Alternator tier;
    private boolean firstTickState = true;
    private ItemStack upgradeStack = ItemStack.EMPTY;

    public BEAlternator(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, Tier.Alternator tier) {
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
        double amps = tier.getAmperage() * getAmperageMultiplier() * getRpmFraction();
        return TransferPack.ampsVoltage(amps, getVoltage());
    }

    public static boolean isStatorUpgrade(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemUpgrade upgrade && upgrade.subtype == SubtypeItemUpgrade.stator;
    }

    public boolean hasStatorUpgrade() {
        return !upgradeStack.isEmpty();
    }

    public ItemStack getStatorUpgrade() {
        return upgradeStack;
    }

    public double getAmperageMultiplier() {
        return hasStatorUpgrade() ? 1.0 + Config.getStatorBonus() : 1.0;
    }

    public void insertStatorUpgrade(ItemStack stack) {
        if (hasStatorUpgrade() || !isStatorUpgrade(stack)) {
            return;
        }
        upgradeStack = stack.split(1);
        notifyUpdate();
    }

    public ItemStack removeStatorUpgrade() {
        ItemStack removed = upgradeStack;
        upgradeStack = ItemStack.EMPTY;
        notifyUpdate();
        return removed;
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
        double maxStress = tier.getMaxStress();
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
                double available = getProduced().getJoules();

                ICapabilityElectrodynamic neighbor = outputCache.getCapability();
                if (neighbor != null && neighbor.isEnergyReceiver()) {
                    neighbor.receivePower(TransferPack.joulesVoltage(available, getVoltage()), false);
                }
            }
        }
    }

    public void firstTick() {
        updateCache();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!upgradeStack.isEmpty()) {
            tag.put(UPGRADE_TAG, upgradeStack.save(registries));
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        upgradeStack = tag.contains(UPGRADE_TAG)
                ? ItemStack.parseOptional(registries, tag.getCompound(UPGRADE_TAG))
                : ItemStack.EMPTY;
    }

    public void updateCache() {
        if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Direction side = getOutputDirection();
            outputCache = BlockCapabilityCache.create(
                    VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, serverLevel, getBlockPos().relative(side), side.getOpposite(),
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
}
