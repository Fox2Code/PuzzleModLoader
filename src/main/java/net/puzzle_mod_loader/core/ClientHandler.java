package net.puzzle_mod_loader.core;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.events.client.ClientJoinEvent;
import net.puzzle_mod_loader.events.client.ClientTickEvent;
import net.puzzle_mod_loader.events.gui.ChangeGuiEvent;
import net.puzzle_mod_loader.events.render.GuiRenderEvent;
import net.puzzle_mod_loader.events.render.PostUIRenderEvent;
import net.puzzle_mod_loader.helper.ModList;
import net.puzzle_mod_loader.launch.Launch;
import net.puzzle_mod_loader.launch.event.EventPriority;
import net.puzzle_mod_loader.launch.event.SubscribeEvent;
import net.puzzle_mod_loader.utils.anim.LinearAnim;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class ClientHandler {
    static void init() {
        EventManager.registerListener(new ClientHandler());
    }

    private static final String LOADING_MESSAGE = "Loading Game Data...";
    private static final boolean DEBUG_UI = false;

    boolean on;
    boolean preCall;
    LinearAnim linearAnim = new LinearAnim(-8, 16, 200);

    @SubscribeEvent
    public void guiRender(GuiRenderEvent event) {
        if (event.getGui() instanceof TitleScreen || event.getGui() instanceof PauseScreen) {
            int size = ModLoader.getMods().size();
            String text;
            switch (size) {
                case 0:
                    text = "No Mods Loaded!";
                    break;
                case 1:
                    text = "1 Mod Loaded!";
                    break;
                default:
                    text = size+" Mods Loaded!";
            }
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), "Puzzle Mod Loader", 2F, 2F, 0xFFFFFF);
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), text, 2F, 11F, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void guiChange(ChangeGuiEvent event) {
        if (!this.preCall) {
            this.preCall = true;
            PreLoader.preloadAsync();
            //new Throwable().printStackTrace();
        }
        if (!DEBUG_UI) {
            return;
        }
        boolean newOn = Minecraft.getInstance().level != null;
        if (this.on != newOn) {
            this.linearAnim.reset();
            this.on = newOn;
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (this.on) {
            linearAnim.setDirection(Launch.lastLoadedClass > (System.currentTimeMillis() - 1000));
        }
    }

    @SubscribeEvent
    public void uiRender(PostUIRenderEvent event) {
        int pos = linearAnim.getValue();
        if (this.on && pos != -8) {
            Font font = Minecraft.getInstance().font;
            int tmp = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            if (pos > 8) {
                GL11.glPushMatrix();
                GL11.glScaled(0.5F, 0.5F, 0.5F);
                font.drawShadow(event.getPoseStack(), Launch.lastLoadedClassName, tmp - (font.width(Launch.lastLoadedClassName)/2F), (pos - 8) * 2, new Color(255, 255, 255).getRGB());
                GL11.glPopMatrix();
            }
            tmp = (tmp) - font.width(LOADING_MESSAGE);
            int color = 150 + Math.abs((int) (100-((System.currentTimeMillis()/2)%200)));
            font.drawShadow(event.getPoseStack(), LOADING_MESSAGE, tmp/2F, pos, new Color(color, color, color).getRGB());
        }
    }

    private static final ResourceLocation PUZZLE_MODS = new ResourceLocation("puzzle:mods");

    @SubscribeEvent(priority = EventPriority.ASYNC)
    public void join(ClientJoinEvent event) {
        if (!ServerHelper.isOnBlacklistedServer()) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            try {
                ModList.encode(byteBuf, ModLoader.getModList());
            } catch (IOException ignored) {}
            event.getPacketListener().getConnection().send(new ServerboundCustomPayloadPacket(PUZZLE_MODS, byteBuf));
        }
    }
}
