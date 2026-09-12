package io.github.kobaltromero.wattz.content.alternator;

import java.util.function.Supplier;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import io.github.kobaltromero.wattz.tier.AlternatorTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public class AlternatorBlock extends DirectionalKineticBlock implements IBE<AlternatorBlockEntity>, IRotate, com.simibubi.create.content.equipment.wrench.IWrenchable, voltaic.prefab.tile.IWrenchable {
    private final AlternatorTier tier;

    private final Supplier<BlockEntityType<AlternatorBlockEntity>> blockEntityType;

    public AlternatorTier getTier() {
        return tier;
    }

    public AlternatorBlock(Properties properties, AlternatorTier tier, Supplier<BlockEntityType<AlternatorBlockEntity>> blockEntityType) {
        super(properties);
        this.tier = tier;
        this.blockEntityType = blockEntityType;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.MOTOR_BLOCK.get(state.getValue(FACING));
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
    public BlockEntityType<? extends AlternatorBlockEntity> getBlockEntityType() {
        return blockEntityType.get();
    }

    @Override
    public Class<AlternatorBlockEntity> getBlockEntityClass() {
        return AlternatorBlockEntity.class;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        if (blockEntity instanceof AlternatorBlockEntity alternator) {
            alternator.updateCache();
        }
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (world instanceof ServerLevel serverLevel) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), player);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return InteractionResult.SUCCESS;
            } else {
                if (player != null && !player.isCreative()) {
                    Block.getDrops(state, serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand())
                            .forEach((itemStack) -> player.getInventory().placeItemBackInInventory(itemStack));
                }
                world.destroyBlock(pos, false);
                return InteractionResult.SUCCESS;
            }
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void onRotate(ItemStack itemStack, BlockPos blockPos, Player player) {
        Level level = player.level();
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(blockPos);
            Direction targetedFace = Direction.UP;
            if (player.getPickRadius() > 0) {
                net.minecraft.world.phys.HitResult hitResult = player.pick(player.getPickRadius(), 1.0F, false);
                if (hitResult instanceof net.minecraft.world.phys.BlockHitResult blockHit) {
                    targetedFace = blockHit.getDirection();
                }
            }

            BlockState rotated = this.getRotatedBlockState(state, targetedFace);

            if (!rotated.canSurvive(level, blockPos)) {
                return;
            }

            KineticBlockEntity.switchToBlockState(level, blockPos, Block.updateFromNeighbourShapes(rotated, level, blockPos));
        }
    }

    @Override
    public void onPickup(ItemStack itemStack, BlockPos blockPos, Player player) {
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            BlockState state = level.getBlockState(blockPos);

            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, blockPos, state, player);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return;
            }

            if (player != null && !player.isCreative()) {
                Block.getDrops(state, serverLevel, blockPos, level.getBlockEntity(blockPos), player, itemStack)
                        .forEach((drop) -> player.getInventory().placeItemBackInInventory(drop));
            }

            level.destroyBlock(blockPos, false, player);
        }
    }
}
