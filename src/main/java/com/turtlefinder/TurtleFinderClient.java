package com.turtlefinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TurtleFinderClient implements ClientModInitializer {
    private static final int SEARCH_RADIUS = 64;
    private static final int UI_COLOR = 0x00FF00; // Green
    private static final int HIGHLIGHT_COLOR = 0xFFFF00; // Yellow
    
    private List<TurtleEntity> nearbyTurtles = new ArrayList<>();
    private int tickCounter = 0;
    
    @Override
    public void onInitializeClient() {
        // Register tick event to search for turtles
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                tickCounter++;
                // Update turtle list every 20 ticks (1 second)
                if (tickCounter >= 20) {
                    updateTurtleList(client);
                    tickCounter = 0;
                }
            }
        });
        
        // Register HUD render callback for UI
        HudRenderCallback.EVENT.register(this::renderTurtleCounter);
        
        // Register world render callback for highlighting
        WorldRenderEvents.AFTER_ENTITIES.register(this::renderTurtleHighlights);
    }
    
    private void updateTurtleList(MinecraftClient client) {
        nearbyTurtles.clear();
        
        if (client.player == null || client.world == null) return;
        
        Vec3d playerPos = client.player.getPos();
        World world = client.world;
        
        // Search for all turtle entities within radius
        world.getEntitiesByClass(TurtleEntity.class, 
            client.player.getBoundingBox().expand(SEARCH_RADIUS), 
            turtle -> true
        ).forEach(turtle -> {
            if (turtle.isAlive()) {
                nearbyTurtles.add(turtle);
            }
        });
    }
    
    private void renderTurtleCounter(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Position on middle left side
        int x = 10;
        int y = screenHeight / 2 - 10;
        
        String turtleText = "Turtles: " + nearbyTurtles.size();
        
        // Draw background rectangle
        drawContext.fill(x - 2, y - 2, x + 80, y + 12, 0x80000000); // Semi-transparent black
        
        // Draw text
        drawContext.drawText(client.textRenderer, Text.literal(turtleText), x, y, UI_COLOR, true);
    }
    
    private void renderTurtleHighlights(WorldRenderEvents.AfterEntitiesContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || nearbyTurtles.isEmpty()) return;
        
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        
        for (TurtleEntity turtle : nearbyTurtles) {
            if (!turtle.isAlive()) continue;
            
            Vec3d turtlePos = turtle.getPos();
            
            // Check if turtle is visible to player
            if (isEntityVisible(client, turtle)) {
                // Draw highlight box around turtle
                renderHighlightBox(matrices, turtlePos, cameraPos);
                
                // Draw line from crosshair to turtle
                renderLineToTurtle(matrices, turtlePos, cameraPos, client);
            }
        }
    }
    
    private boolean isEntityVisible(MinecraftClient client, TurtleEntity turtle) {
        // Simple visibility check - you might want to implement proper frustum culling
        Vec3d playerPos = client.player.getPos();
        Vec3d turtlePos = turtle.getPos();
        double distance = playerPos.distanceTo(turtlePos);
        
        // Consider visible if within reasonable range and not obstructed by terrain
        return distance <= 32.0; // Reduced range for visible highlighting
    }
    
    private void renderHighlightBox(MatrixStack matrices, Vec3d turtlePos, Vec3d cameraPos) {
        matrices.push();
        
        // Translate to turtle position relative to camera
        matrices.translate(
            turtlePos.x - cameraPos.x,
            turtlePos.y - cameraPos.y,
            turtlePos.z - cameraPos.z
        );
        
        // This is where you'd implement the actual box rendering
        // For simplicity, this is a placeholder - you'd need to use RenderSystem
        // and draw lines or quads to create the highlight effect
        
        matrices.pop();
    }
    
    private void renderLineToTurtle(MatrixStack matrices, Vec3d turtlePos, Vec3d cameraPos, MinecraftClient client) {
        // Get screen center (crosshair position)
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // This would require converting world coordinates to screen coordinates
        // and drawing a line - implementation would need more complex rendering code
        
        // Placeholder for line rendering logic
    }
}
