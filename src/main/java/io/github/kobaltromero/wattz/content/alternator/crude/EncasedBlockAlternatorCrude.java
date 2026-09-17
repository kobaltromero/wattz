package io.github.kobaltromero.wattz.content.alternator.crude;

import java.util.function.Supplier;

import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EncasedBlockAlternatorCrude extends DirectionalKineticBlock implements IBE<BEAlternatorCrude>, IRotate,
        com.simibubi.create.content.equipment.wrench.IWrenchable, voltaic.prefab.tile.IWrenchable, EncasedBlock {

    private final Supplier<BlockEntityType<BEAlternatorCrude>> blockEntityType;
    private final Supplier<? extends Block> casing;
    private final Supplier<? extends Block> alternator;

    public EncasedBlockAlternatorCrude(Properties properties, Supplier<BlockEntityType<BEAlternatorCrude>> blockEntityType,
                                       Supplier<? extends Block> casing, Supplier<? extends Block> alternator) {
        super(properties);
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
    public BlockEntityType<? extends BEAlternatorCrude> getBlockEntityType() {
        return blockEntityType.get();
    }

    @Override
    public Class<BEAlternatorCrude> getBlockEntityClass() {
        return BEAlternatorCrude.class;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        if (blockEntity instanceof BEAlternatorCrude alternator) {
            alternator.updateCache();
        }
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        level.levelEvent(2001, pos, Block.getId(state));
        KineticBlockEntity.switchToBlockState(level, pos,
                alternator.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
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

            if (level.getBlockEntity(blockPos) instanceof BEAlternatorCrude alternator) {
                alternator.updateCache();
            }
        }
    }

    @Override
    public void onPickup(ItemStack stack, BlockPos blockPos, Player player) {
        BlockHitResult result = new BlockHitResult(
                Vec3.atCenterOf(blockPos),
                player.getDirection().getOpposite(),
                blockPos,
                false
        );

        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, result);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState state = level.getBlockState(pos);

        level.levelEvent(2001, pos, Block.getId(state));
        KineticBlockEntity.switchToBlockState(level, pos,
                alternator.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
    }

    @Override
    public Block getCasing() {
        return casing.get();
    }

    @Override
    public void handleEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand, BlockHitResult ray) {
        KineticBlockEntity.switchToBlockState(level, pos,
                this.defaultBlockState().setValue(FACING, state.getValue(FACING)));
    }
}
