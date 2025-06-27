package com.example.turtlefinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TurtleFinderMod implements ClientModInitializer {

    public static final List<Vec3d> turtlePositions = new ArrayList<>();

    @Override
    public void onInitializeClient() {

        // Tick update
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                turtlePositions.clear();
                for (var entity : client.world.getEntities()) {
                    if (entity instanceof TurtleEntity) {
                        turtlePositions.add(entity.getPos());
                    }
                }
            }
        });

        // HUD display
        HudRenderCallback.EVENT.register((DrawContext drawContext, float tickDelta) -> {
            drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                "Turtles: " + turtlePositions.size(),
                10, drawContext.getScaledWindowHeight() / 2, 0x00FF00, true
            );
        });
    }
}
