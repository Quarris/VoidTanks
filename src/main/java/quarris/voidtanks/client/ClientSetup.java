package quarris.voidtanks.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import quarris.voidtanks.VoidTanks;
import quarris.voidtanks.content.TankTile;

public class ClientSetup {

    public static void setup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(TankTile.TYPE, TankRenderer::new);
        RenderTypeLookup.setRenderLayer(VoidTanks.SMALL_TANK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(VoidTanks.MEDIUM_TANK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(VoidTanks.LARGE_TANK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(VoidTanks.HUGE_TANK, RenderType.getCutout());
    }

}
