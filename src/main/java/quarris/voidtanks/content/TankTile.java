package quarris.voidtanks.content;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import quarris.voidtanks.VoidTanks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TankTile extends TileEntity {

    public static final TileEntityType<TankTile> TYPE = TileEntityType.Builder
            .create(TankTile::new, VoidTanks.SMALL_TANK, VoidTanks.MEDIUM_TANK, VoidTanks.LARGE_TANK, VoidTanks.HUGE_TANK)
            .build(null);

    private FluidTank tank;
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

    public TankTile() {
        this(0);
    }

    public TankTile(int buckets) {
        super(TYPE);
        this.tank = new FluidTank(buckets * FluidAttributes.BUCKET_VOLUME) {
            @Override
            public int fill(FluidStack resource, FluidAction action) {
                int filled = super.fill(resource, action);
                return TankTile.this.isVoid() && resource.isFluidEqual(this.getFluid()) ? resource.getAmount() : filled;
            }

            @Override
            protected void onContentsChanged() {
                TankTile.this.sendToClients();
            }
        };
    }

    public FluidStack getFluid() {
        return this.tank.getFluid();
    }

    public FluidTank getTank() {
        return this.tank;
    }

    public boolean isVoid() {
        return this.world.getBlockState(this.pos).get(TankBlock.IS_VOID);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.read(state, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    public void sendToClients() {
        if (this.getWorld().isRemote) {
            VoidTanks.LOGGER.debug("Tried to sync to clients from a client.");
            return;
        }

        ServerWorld world = (ServerWorld) this.getWorld();
        Stream<ServerPlayerEntity> entities = world.getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(this.getPos()), false);
        SUpdateTileEntityPacket packet = this.getUpdatePacket();
        entities.forEach(e -> e.connection.sendPacket(packet));
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        tank.readFromNBT(tag);
        tank.setCapacity(tag.getInt("FluidCapacity"));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        tank.writeToNBT(tag);
        tag.putInt("FluidCapacity", tank.getCapacity());
        return tag;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(capability, facing);
    }
}
