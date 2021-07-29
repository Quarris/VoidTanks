package quarris.voidtanks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quarris.voidtanks.client.ClientSetup;
import quarris.voidtanks.content.TankBlock;

@Mod(VoidTanks.ID)
public class VoidTanks {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String ID = "voidtanks";

    public static ResourceLocation createRes(String name) {
        return new ResourceLocation(ID, name);
    }

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(HUGE_TANK);
        }
    };

    public static final Block SMALL_TANK = new TankBlock(4);
    public static final Block MEDIUM_TANK = new TankBlock(16);
    public static final Block LARGE_TANK = new TankBlock(64);
    public static final Block HUGE_TANK = new TankBlock(256);

    public static final Item VOID_UPGRADE = new Item(new Item.Properties().tab(ITEM_GROUP));


    public VoidTanks() {
        DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::setup);
            return null;
        });
    }
}
