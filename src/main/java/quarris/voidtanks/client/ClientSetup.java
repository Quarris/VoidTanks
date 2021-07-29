package quarris.voidtanks.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import quarris.voidtanks.VoidTanks;
import quarris.voidtanks.content.TankTile;

public class ClientSetup {

    public static void setup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(TankTile.TYPE, TankRenderer::new);
        ItemBlockRenderTypes.setRenderLayer(VoidTanks.SMALL_TANK, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(VoidTanks.MEDIUM_TANK, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(VoidTanks.LARGE_TANK, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(VoidTanks.HUGE_TANK, RenderType.cutoutMipped());
    }

}
