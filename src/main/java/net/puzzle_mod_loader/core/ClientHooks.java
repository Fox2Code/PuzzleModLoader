package net.puzzle_mod_loader.core;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.events.client.*;
import net.puzzle_mod_loader.events.gui.ChangeGuiEvent;
import net.puzzle_mod_loader.events.gui.GuiInitEvent;
import net.puzzle_mod_loader.events.gui.GuiResizeEvent;
import net.puzzle_mod_loader.events.render.GuiPreRenderEvent;
import net.puzzle_mod_loader.events.render.GuiRenderEvent;
import net.puzzle_mod_loader.events.render.PostUIRenderEvent;
import net.puzzle_mod_loader.events.render.PostWorldRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.AbstractResourcePack;
import net.minecraft.server.packs.FileResourcePack;
import net.minecraft.server.packs.FolderResourcePack;
import net.minecraft.server.packs.Pack;
import net.minecraft.util.profiling.ProfilerFiller;
import net.puzzle_mod_loader.utils.ReflectedClass;

import java.io.File;
import java.util.List;

public class ClientHooks {
    private static File loader = new File(ClientHooks.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    private static AbstractResourcePack resourcePack = loader.isDirectory()? new FolderResourcePack(loader) : new FileResourcePack(loader);

    private static ProfilerFiller getProfiler() {
        return Minecraft.getInstance().getProfiler();
    }

    public static ChangeGuiEvent clientChangeGui(Screen from, Screen to) {
        ChangeGuiEvent changeGuiEvent = new ChangeGuiEvent(from, to);
        EventManager.processEvent(changeGuiEvent);
        return changeGuiEvent;
    }

    public static void clientGuiInit(Screen gui, Minecraft minecraft, int w, int h) {
        gui.init(minecraft, w, h);
        EventManager.processEvent(new GuiInitEvent(gui));
    }

    public static void clientGuiResize(Screen gui, Minecraft minecraft, int w, int h) {
        gui.resize(minecraft, w, h);
        EventManager.processEvent(new GuiResizeEvent(gui));
    }

    public static void clientGuiRender(Screen gui, PoseStack poseStack, int var1, int var2, float var3) {
        getProfiler().push("puzzle");
        EventManager.processEvent(new GuiPreRenderEvent(gui, poseStack));
        getProfiler().pop();
        gui.render(poseStack, var1, var2, var3);
        getProfiler().push("puzzle");
        EventManager.processEvent(new GuiRenderEvent(gui, poseStack));
        getProfiler().pop();
    }

    public static void postWorld(PoseStack poseStack) {
        getProfiler().push("puzzle");
        EventManager.processEvent(new PostWorldRenderEvent(poseStack));
        getProfiler().pop();
    }

    public static List<Pack> hookPacks(List<Pack> resourcePacks) {
        for (Mod mod:ModLoader.getMods()) {
            resourcePacks.add(1, mod.dataPack);
        }
        resourcePacks.add(resourcePack);
        ModLoader.LOGGER.info(ModLoader.getMods().size()+" mods injected into the resource loader!");
        return resourcePacks;
    }

    public static void postUIRender() {
        getProfiler().push("puzzle");
        EventManager.processEvent(new PostUIRenderEvent(new PoseStack()));
        getProfiler().pop();
    }

    public static void clientTick() {
        getProfiler().push("puzzle");
        EventManager.processEvent(new ClientTickEvent());
        getProfiler().pop();
    }

    public static void pingHook(ServerData serverData, ServerStatusPinger pinger) {
        EventManager.processEvent(new ClientPingEvent(serverData, pinger));
    }

    public static ClientIntentionPacket preJoinHook(ClientIntentionPacket clientIntentionPacket) {
        ClientPreJoinEvent clientPreJoinEvent;
        EventManager.processEvent(clientPreJoinEvent = new ClientPreJoinEvent(Minecraft.getInstance().getCurrentServer(), clientIntentionPacket.getProtocolVersion()));
        try {
            ReflectedClass.of(clientIntentionPacket).set("protocolVersion",clientPreJoinEvent.getProtocol());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return clientIntentionPacket;
    }

    public static void joinHook(ClientPacketListener clientPacketListener) {
        EventManager.processEvent(new ClientJoinEvent(Minecraft.getInstance().getCurrentServer(), clientPacketListener));
    }

    public static void onDisconnect(Component component) {
        EventManager.processEvent(new ClientDisconnectEvent(component));
    }
}
