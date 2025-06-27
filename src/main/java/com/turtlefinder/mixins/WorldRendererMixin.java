package com.example.turtlefinder.mixin;

import com.example.turtlefinder.TurtleFinderMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.vertex.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
                          net.minecraft.client.render.Camera camera, GameRenderer gameRenderer,
                          LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix,
                          CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        Vec3d cameraPos = camera.getPos();

        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(2.0f);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

        for (Vec3d targetPos : TurtleFinderMod.turtlePositions) {
            Vec3d start = new Vec3d(0, 0, 0); // from camera (center)
            Vec3d end = targetPos.subtract(cameraPos);

            buffer.vertex(start.x, start.y, start.z).color(0, 255, 0, 255).next();
            buffer.vertex(end.x, end.y, end.z).color(0, 255, 0, 255).next();
        }

        BufferRenderer.drawWithShader(buffer.end());
        RenderSystem.enableDepthTest();
    }
}
