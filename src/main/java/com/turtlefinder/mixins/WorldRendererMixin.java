package com.turtlefinder.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    
    @Shadow @Final private MinecraftClient client;
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (client.player == null || client.world == null) return;
        
        Vec3d cameraPos = camera.getPos();
        MatrixStack matrices = new MatrixStack();
        
        // Find nearby turtles and render highlights
        client.world.getEntitiesByClass(TurtleEntity.class, 
            client.player.getBoundingBox().expand(32), 
            turtle -> turtle.isAlive()
        ).forEach(turtle -> {
            if (isEntityVisible(turtle, cameraPos)) {
                renderTurtleEffects(matrices, turtle, cameraPos);
            }
        });
    }
    
    private boolean isEntityVisible(TurtleEntity turtle, Vec3d cameraPos) {
        Vec3d turtlePos = turtle.getPos();
        double distance = cameraPos.distanceTo(turtlePos);
        return distance <= 32.0;
    }
    
    private void renderTurtleEffects(MatrixStack matrices, TurtleEntity turtle, Vec3d cameraPos) {
        Vec3d turtlePos = turtle.getPos();
        
        matrices.push();
        matrices.translate(
            turtlePos.x - cameraPos.x,
            turtlePos.y - cameraPos.y,
            turtlePos.z - cameraPos.z
        );
        
        // Set up rendering
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        
        // Render highlight box
        renderHighlightBox(matrices);
        
        // Render line to crosshair
        renderLineToCrosshair(matrices, turtlePos, cameraPos);
        
        // Restore render state
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        
        matrices.pop();
    }
    
    private void renderHighlightBox(MatrixStack matrices) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        float minX = -0.5f, minY = 0f, minZ = -0.5f;
        float maxX = 0.5f, maxY = 1.5f, maxZ = 0.5f;
        
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        // Bottom face
        buffer.vertex(matrix, minX, minY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, minY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, minY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, minY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, minY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, minY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, minY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, minY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        
        // Top face
        buffer.vertex(matrix, minX, maxY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, maxY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, maxY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, maxY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, maxY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, maxY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, maxY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, maxY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        
        // Vertical edges
        buffer.vertex(matrix, minX, minY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, maxY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, minY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, maxY, minZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, minY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, maxX, maxY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, minY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        buffer.vertex(matrix, minX, maxY, maxZ).color(1.0f, 1.0f, 0.0f, 0.8f).next();
        
        tessellator.draw();
    }
    
    private void renderLineToCrosshair(MatrixStack matrices, Vec3d turtlePos, Vec3d cameraPos) {
        if (client.player == null) return;
        
        Vec3d eyePos = client.player.getEyePos();
        Vec3d lookVec = client.player.getRotationVec(1.0f);
        
        // Calculate crosshair position in world (just in front of player)
        Vec3d crosshairPos = eyePos.add(lookVec.multiply(0.1));
        
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        // Line from turtle to crosshair position
        Vec3d relativeStart = Vec3d.ZERO; // We're already translated to turtle position
        Vec3d relativeEnd = crosshairPos.subtract(turtlePos);
        
        buffer.vertex(matrix, (float)relativeStart.x, (float)relativeStart.y + 0.75f, (float)relativeStart.z)
              .color(0.0f, 1.0f, 0.0f, 0.6f).next();
        buffer.vertex(matrix, (float)relativeEnd.x, (float)relativeEnd.y, (float)relativeEnd.z)
              .color(0.0f, 1.0f, 0.0f, 0.6f).next();
        
        tessellator.draw();
    }
}
