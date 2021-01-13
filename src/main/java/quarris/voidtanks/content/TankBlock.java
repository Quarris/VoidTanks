package quarris.voidtanks.content;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import quarris.voidtanks.VoidTanks;

import java.util.List;

public class TankBlock extends ContainerBlock {

    public static final BooleanProperty IS_VOID = BooleanProperty.create("is_void");

    private static final VoxelShape TOP_CAP = VoxelShapes.create(0, 15 / 16d, 0, 1, 1, 1);
    private static final VoxelShape CENTER = VoxelShapes.create(1 / 16d, 1 / 16d, 1 / 16d, 15 / 16d, 15 / 16d, 15 / 16d);
    private static final VoxelShape BOTTOM_CAP = VoxelShapes.create(0, 0, 0, 1, 1 / 16d, 1);
    private static final VoxelShape TANK_SHAPE = VoxelShapes.or(CENTER, VoxelShapes.or(TOP_CAP, BOTTOM_CAP));

    public final int buckets;

    public TankBlock(int buckets) {
        super(Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(2.0F).notSolid());
        this.setDefaultState(this.getStateContainer().getBaseState().with(IS_VOID, false));
        this.buckets = buckets;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_VOID);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack held = player.getHeldItem(hand);
        if (!state.get(IS_VOID) && held.getItem() == VoidTanks.VOID_UPGRADE) {
            world.setBlockState(pos, state.with(IS_VOID, true));
            if (!player.isCreative()) {
                held.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }

        if (state.get(IS_VOID) && player.isSneaking()) {
            world.setBlockState(pos, state.with(IS_VOID, false));
            if (!world.isRemote) {
                ItemStack obsidian = new ItemStack(VoidTanks.VOID_UPGRADE);
                if (!player.isCreative() && !player.addItemStackToInventory(obsidian)) {
                    InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosYEye(), player.getPosZ(), obsidian);
                }
            }
        }

        if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, hit.getFace()) ||
                held.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (state.get(IS_VOID))
            drops.add(new ItemStack(VoidTanks.VOID_UPGRADE));

        return drops;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return TANK_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TankTile(this.buckets);
    }
}
