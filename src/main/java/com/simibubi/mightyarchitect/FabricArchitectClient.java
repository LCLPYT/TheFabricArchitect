package com.simibubi.mightyarchitect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.control.ArchitectManager;
import com.simibubi.mightyarchitect.control.SchematicRenderer;
import com.simibubi.mightyarchitect.control.phase.PrintingToMultiplayer;
import com.simibubi.mightyarchitect.event.ChatReceivedClientCallback;
import com.simibubi.mightyarchitect.event.LevelRenderLastCallback;
import com.simibubi.mightyarchitect.foundation.SuperRenderTypeBuffer;
import com.simibubi.mightyarchitect.foundation.utility.AnimationTickHolder;
import com.simibubi.mightyarchitect.foundation.utility.ShaderManager;
import com.simibubi.mightyarchitect.foundation.utility.outliner.Outliner;
import com.simibubi.mightyarchitect.gui.ScreenHelper;
import com.simibubi.mightyarchitect.networking.IPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import static com.simibubi.mightyarchitect.TheFabricArchitect.NAME;

@Environment(EnvType.CLIENT)
public class FabricArchitectClient implements ClientModInitializer {

    public static KeyMapping COMPOSE;
    public static KeyMapping TOOL_MENU;

    public static SchematicRenderer renderer = new SchematicRenderer();
    public static Outliner outliner = new Outliner();

    @Override
    public void onInitializeClient() {
        // TODO init color handlers
        COMPOSE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "keybind.mightyarchitect.start_composing",
                GLFW.GLFW_KEY_G,
                NAME));
        TOOL_MENU = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "keybind.mightyarchitect.tool_menu",
                GLFW.GLFW_KEY_LEFT_ALT,
                NAME));

        ClientTickEvents.START_CLIENT_TICK.register(FabricArchitectClient::onStartTick);
        LevelRenderLastCallback.EVENT.register(FabricArchitectClient::onRenderWorldLast);
        ClientTickEvents.START_CLIENT_TICK.register(ShaderManager::onClientTick);
        ClientTickEvents.START_CLIENT_TICK.register(ScreenHelper::onClientTick);
        ChatReceivedClientCallback.EVENT.register(PrintingToMultiplayer::onCommandFeedback);

        ArchitectManager.registerEvents();
    }

    private static void onStartTick(Minecraft client) {
        AnimationTickHolder.tick();

        if (!isGameActive())
            return;

        ArchitectManager.tickBlockHighlightOutlines();
        outliner.tickOutlines();
        renderer.tick();
    }

    private static void onRenderWorldLast(PoseStack ms, float partialTicks) {
        Camera info = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 view = info.getPosition();

        ms.pushPose();
        ms.translate(-view.x(), -view.y(), -view.z());
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance()
                .renderBuffers()
                .bufferSource();

        SuperRenderTypeBuffer b = SuperRenderTypeBuffer.getInstance();

        renderer.render(ms, b);
        ArchitectManager.render(ms, b);
        outliner.renderOutlines(ms, b);

        b.draw();
        buffer.endBatch();
        ms.popPose();
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }

    public static void sendToServer(IPacket packet) {
        final var buf = PacketByteBufs.create();
        packet.toBytes(buf);
        ClientPlayNetworking.send(packet.getId(), buf);
    }
}
