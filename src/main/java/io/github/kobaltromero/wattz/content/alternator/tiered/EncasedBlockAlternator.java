package io.github.kobaltromero.wattz.content.alternator.tiered;

import java.util.function.Supplier;

import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import io.github.kobaltromero.wattz.tier.Tier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public class EncasedBlockAlternator extends DirectionalKineticBlock implements IBE<BEAlternator>, IRotate,
        com.simibubi.create.content.equipment.wrench.IWrenchable, voltaic.prefab.tile.IWrenchable, EncasedBlock {
    private final Tier.Alternator tier;

    private final Supplier<BlockEntityType<BEAlternator>> blockEntityType;
    private final Supplier<? extends Block> casing;
    private final Supplier<? extends Block> alternator;

    public Tier.Alternator getTier() {
        return tier;
    }

    public EncasedBlockAlternator(Properties properties, Tier.Alternator tier, Supplier<BlockEntityType<BEAlternator>> blockEntityType,
                                  Supplier<? extends Block> casing, Supplier<? extends Block> alternator) {
        super(properties);
        this.tier = tier;
        this.blockEntityType = blockEntityType;
        this.casing = casing;
        this.alternator = alternator;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        return (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) && preferred != null
                ? defaultBlockState().setValue(FACING, preferred)
                : super.getStateForPlacement(context);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public BlockEntityType<? extends BEAlternator> getBlockEntityType() {
        return blockEntityType.get();
    }

    @Override
    public Class<BEAlternator> getBlockEntityClass() {
        return BEAlternator.class;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!isSameAlternator(newState) && level.getBlockEntity(pos) instanceof BEAlternator alternator
                && alternator.hasStatorUpgrade()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), alternator.removeStatorUpgrade());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private boolean isSameAlternator(BlockState newState) {
        return newState.getBlock() instanceof IBE<?> ibe && ibe.getBlockEntityType() == getBlockEntityType();
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        if (blockEntity instanceof BEAlternator alternator) {
            alternator.updateCache();
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof BEAlternator alternator && BEAlternator.isStatorUpgrade(stack)) {
            if (alternator.hasStatorUpgrade()) {
                return ItemInteractionResult.FAIL;
            }
            if (!level.isClientSide) {
                alternator.insertStatorUpgrade(stack);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof BEAlternator alternator && alternator.hasStatorUpgrade()) {
            if (!level.isClientSide) {
                ItemStack removed = alternator.removeStatorUpgrade();
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false);
                }
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack preservedUpgrade = level.getBlockEntity(pos) instanceof BEAlternator existing
                ? existing.getStatorUpgrade().copy()
                : ItemStack.EMPTY;
        level.levelEvent(2001, pos, Block.getId(state));
        KineticBlockEntity.switchToBlockState(level, pos,
                alternator.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
        if (!preservedUpgrade.isEmpty() && level.getBlockEntity(pos) instanceof BEAlternator updated) {
            updated.insertStatorUpgrade(preservedUpgrade);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRotate(ItemStack itemStack, BlockPos blockPos, Player player) {
        Level level = player.level();
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(blockPos);
            Direction targetedFace = Direction.UP;
            BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                targetedFace = hitResult.getDirection();
            }

            BlockState rotated = this.getRotatedBlockState(state, targetedFace);

            if (!rotated.canSurvive(level, blockPos)) {
                return;
            }

            KineticBlockEntity.switchToBlockState(level, blockPos, Block.updateFromNeighbourShapes(rotated, level, blockPos));

            if (level.getBlockEntity(blockPos) instanceof BEAlternator alternator) {
                alternator.updateCache();
            }
        }
    }

    @Override
    public void onPickup(ItemStack stack, BlockPos blockPos, Player player) {
        Level level = player.level();
        if (level instanceof ServerLevel) {
            BlockState state = level.getBlockState(blockPos);

            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, blockPos, state, player);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return;
            }
            level.destroyBlock(blockPos, true, player);
        }
    }

    @Override
    public Block getCasing() {
        return casing.get();
    }

    @Override
    public void handleEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand, BlockHitResult ray) {
        ItemStack preservedUpgrade = level.getBlockEntity(pos) instanceof BEAlternator existing
                ? existing.getStatorUpgrade().copy()
                : ItemStack.EMPTY;
        KineticBlockEntity.switchToBlockState(level, pos,
                this.defaultBlockState().setValue(FACING, state.getValue(FACING)));
        if (!preservedUpgrade.isEmpty() && level.getBlockEntity(pos) instanceof BEAlternator updated) {
            updated.insertStatorUpgrade(preservedUpgrade);
        }
    }
}
