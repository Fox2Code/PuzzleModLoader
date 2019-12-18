package net.puzzle_mod_loader.core;

import net.minecraft.client.gui.Font;
import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.events.client.ClientTickEvent;
import net.puzzle_mod_loader.events.gui.ChangeGuiEvent;
import net.puzzle_mod_loader.events.render.GuiRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.puzzle_mod_loader.events.render.PostUIRenderEvent;
import net.puzzle_mod_loader.launch.Launch;
import net.puzzle_mod_loader.launch.event.SubscribeEvent;
import net.puzzle_mod_loader.utils.anim.LinearAnim;
import org.lwjgl.opengl.GL11;

import java.awt.*;

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
            Minecraft.getInstance().font.drawShadow("Puzzle Mod Loader", 2F, 2F, 0xFFFFFF);
            Minecraft.getInstance().font.drawShadow(text, 2F, 11F, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void guiChange(ChangeGuiEvent event) {
        if (!this.preCall) {
            this.preCall = true;
            PreLoader.preloadAsync();
            new Throwable().printStackTrace();
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
                font.drawShadow(Launch.lastLoadedClassName, tmp - (font.width(Launch.lastLoadedClassName)/2F), (pos - 8) * 2, new Color(255, 255, 255).getRGB());
                GL11.glPopMatrix();
            }
            tmp = (tmp) - font.width(LOADING_MESSAGE);
            int color = 150 + Math.abs((int) (100-((System.currentTimeMillis()/2)%200)));
            font.drawShadow(LOADING_MESSAGE, tmp/2F, pos, new Color(color, color, color).getRGB());
        }
    }
}
