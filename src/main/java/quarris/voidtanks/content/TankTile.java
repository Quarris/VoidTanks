package quarris.voidtanks.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
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

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class TankTile extends BlockEntity {

    public static final BlockEntityType<TankTile> TYPE = BlockEntityType.Builder
            .of(TankTile::new, VoidTanks.SMALL_TANK, VoidTanks.MEDIUM_TANK, VoidTanks.LARGE_TANK, VoidTanks.HUGE_TANK)
            .build(null);

    private FluidTank tank;
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

    public TankTile(BlockPos pos, BlockState state) {
        this(pos, state, 0);
    }

    public TankTile(BlockPos pos, BlockState state, int buckets) {
        super(TYPE, pos, state);
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
        return this.level.getBlockState(this.worldPosition).getValue(TankBlock.IS_VOID);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    public void sendToClients() {
        if (this.getLevel().isClientSide) {
            VoidTanks.LOGGER.debug("Tried to sync to clients from a client.");
            return;
        }

        ServerLevel world = (ServerLevel) this.getLevel();
        Stream<ServerPlayer> entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.getBlockPos()), false);
        ClientboundBlockEntityDataPacket packet = this.getUpdatePacket();
        entities.forEach(e -> e.connection.send(packet));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.readFromNBT(tag);
        tank.setCapacity(tag.getInt("FluidCapacity"));
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag = super.save(tag);
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
