package quarris.voidtanks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quarris.voidtanks.content.TankBlock;
import quarris.voidtanks.content.TankTile;

@Mod.EventBusSubscriber(modid = VoidTanks.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                VoidTanks.SMALL_TANK.setRegistryName(VoidTanks.createRes("small_tank")),
                VoidTanks.MEDIUM_TANK.setRegistryName(VoidTanks.createRes("medium_tank")),
                VoidTanks.LARGE_TANK.setRegistryName(VoidTanks.createRes("large_tank")),
                VoidTanks.HUGE_TANK.setRegistryName(VoidTanks.createRes("huge_tank"))
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Item.Properties tankProperties = new Item.Properties().group(VoidTanks.ITEM_GROUP);
        event.getRegistry().registerAll(
                new BlockItem(VoidTanks.SMALL_TANK, tankProperties).setRegistryName(VoidTanks.SMALL_TANK.getRegistryName()),
                new BlockItem(VoidTanks.MEDIUM_TANK, tankProperties).setRegistryName(VoidTanks.MEDIUM_TANK.getRegistryName()),
                new BlockItem(VoidTanks.LARGE_TANK, tankProperties).setRegistryName(VoidTanks.LARGE_TANK.getRegistryName()),
                new BlockItem(VoidTanks.HUGE_TANK, tankProperties).setRegistryName(VoidTanks.HUGE_TANK.getRegistryName()),
                VoidTanks.VOID_UPGRADE.setRegistryName(VoidTanks.createRes("void_upgrade"))
        );
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TankTile.TYPE.setRegistryName(VoidTanks.createRes("tank"))
        );
    }

}
