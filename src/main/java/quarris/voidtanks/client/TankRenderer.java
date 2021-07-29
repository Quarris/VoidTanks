package quarris.voidtanks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.fluids.FluidStack;
import quarris.voidtanks.content.TankTile;

public class TankRenderer implements BlockEntityRenderer<TankTile> {

    public TankRenderer(BlockEntityRendererProvider.Context ctx) {

    }

    @Override
    public void render(TankTile tank, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        FluidStack fluidStack = tank.getFluid();
        if (!fluidStack.isEmpty()) {
            int amount = fluidStack.getAmount();
            int total = tank.getTank().getTankCapacity(0);
            this.renderFluidInTank(tank.getLevel(), tank.getBlockPos(), fluidStack, matrix, buffer, amount / (float) total);
        }
    }

    // Ewy was here ;-)
    private void renderFluidInTank(BlockAndTintGetter world, BlockPos pos, FluidStack fluidStack, PoseStack matrix, MultiBufferSource buffer, float fluidPerc) {
        matrix.pushPose();

        matrix.translate(0.5d, 0.5d, 0.5d);
        Matrix4f matrix4f = matrix.last().pose();
        Matrix3f matrix3f = matrix.last().normal();

        int color = fluidStack.getFluid().getAttributes().getColor(fluidStack);
        VertexConsumer builder = buffer.getBuffer(RenderType.waterMask());

        for (int i = 0; i < 4; i++) {
            this.renderNorthFluidFace(this.getFluidFlowingSprite(fluidStack), matrix4f, matrix3f, builder, color, fluidPerc);
            matrix.mulPose(Vector3f.YP.rotationDegrees(90));
        }
        this.renderTopFluidFace(this.getFluidStillSprite(fluidStack), matrix4f, matrix3f, builder, color, fluidPerc);

        matrix.popPose();
    }

    private void renderTopFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, VertexConsumer builder, int color, float fluidPerc) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 10 / 16f;
        float height = 14 / 16f;

        float minU = sprite.getU(3);
        float maxU = sprite.getU(13);
        float minV = sprite.getV(3);
        float maxV = sprite.getV(13);

        builder.vertex(matrix4f, -width / 2, -height / 2 + fluidPerc * height, -width / 2).color(r, g, b, a)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, -width / 2, -height / 2 + fluidPerc * height, width / 2).color(r, g, b, a)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2 + fluidPerc * height, width / 2).color(r, g, b, a)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2 + fluidPerc * height, -width / 2).color(r, g, b, a)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    private void renderNorthFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, VertexConsumer builder, int color, float fluidPerc) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 10 / 16f;
        float height = 14 / 16f;

        float minU = sprite.getU(3);
        float maxU = sprite.getU(13);
        float minV = sprite.getV(1);
        float maxV = sprite.getV(15 * fluidPerc);

        builder.vertex(matrix4f, -width / 2, -height / 2 + height * fluidPerc, -0.3f).color(r, g, b, a)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2 + height * fluidPerc, -0.3f).color(r, g, b, a)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2, -0.3f).color(r, g, b, a)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, -width / 2, -height / 2, -0.3f).color(r, g, b, a)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();
    }

    private TextureAtlasSprite getFluidStillSprite(FluidStack fluidStack) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidStack.getFluid().getAttributes().getStillTexture(fluidStack));
    }

    private TextureAtlasSprite getFluidFlowingSprite(FluidStack fluidStack) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidStack.getFluid().getAttributes().getFlowingTexture(fluidStack));
    }
}
