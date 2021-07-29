package quarris.voidtanks.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import quarris.voidtanks.VoidTanks;

import java.util.List;

public class TankBlock extends BaseEntityBlock implements EntityBlock {

    public static final BooleanProperty IS_VOID = BooleanProperty.create("is_void");

    private static final VoxelShape TOP_CAP = Shapes.box(0, 15 / 16d, 0, 1, 1, 1);
    private static final VoxelShape CENTER = Shapes.box(1 / 16d, 1 / 16d, 1 / 16d, 15 / 16d, 15 / 16d, 15 / 16d);
    private static final VoxelShape BOTTOM_CAP = Shapes.box(0, 0, 0, 1, 1 / 16d, 1);
    private static final VoxelShape TANK_SHAPE = Shapes.or(CENTER, Shapes.or(TOP_CAP, BOTTOM_CAP));

    public final int buckets;

    public TankBlock(int buckets) {
        super(Block.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).strength(2.0F).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(IS_VOID, false));
        this.buckets = buckets;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_VOID);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!state.getValue(IS_VOID) && held.getItem() == VoidTanks.VOID_UPGRADE) {
            world.setBlockAndUpdate(pos, state.setValue(IS_VOID, true));
            if (!player.isCreative()) {
                held.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(IS_VOID) && player.isShiftKeyDown()) {
            world.setBlockAndUpdate(pos, state.setValue(IS_VOID, false));
            if (!world.isClientSide) {
                ItemStack obsidian = new ItemStack(VoidTanks.VOID_UPGRADE);
                if (!player.isCreative() && !player.addItem(obsidian)) {
                    Containers.dropItemStack(world, player.getX(), player.getEyeY(), player.getZ(), obsidian);
                }
            }
        }

        if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, hit.getDirection()) ||
                held.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (state.getValue(IS_VOID))
            drops.add(new ItemStack(VoidTanks.VOID_UPGRADE));

        return drops;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return TANK_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TankTile(pos, state, this.buckets);
    }
}
