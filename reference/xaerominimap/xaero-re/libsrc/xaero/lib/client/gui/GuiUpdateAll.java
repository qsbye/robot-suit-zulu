/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import com.mojang.blaze3d.Blaze3D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.apache.commons.codec.binary.Hex;
import xaero.lib.XaeroLib;
import xaero.lib.client.gui.widget.online.WidgetScreen;
import xaero.lib.patreon.Patreon;
import xaero.lib.patreon.PatreonMod;
import xaero.lib.platform.Services;

public class GuiUpdateAll
extends ConfirmScreen
implements WidgetScreen {
    public GuiUpdateAll() {
        super(GuiUpdateAll::confirmResult, (Component)Component.literal((String)("These mods are out-of-date: " + GuiUpdateAll.modListToNames(Patreon.getOutdatedMods()))), (Component)Component.literal((String)(Patreon.getHasAutoUpdates() ? "Would you like to automatically update them?" : "Would you like to update them (open the mod pages)?")));
        Patreon.setNotificationDisplayed(true);
    }

    private static String modListToNames(List<Object> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(((PatreonMod)list.get((int)i)).modName);
        }
        return builder.toString();
    }

    protected void addButtons(LinearLayout $$0) {
        super.addButtons($$0);
        if (Patreon.getHasAutoUpdates()) {
            this.layout.addChild((LayoutElement)Button.builder((Component)Component.translatable((String)"Changelogs", (Object[])new Object[0]), b -> {
                for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                    PatreonMod mod = (PatreonMod)Patreon.getOutdatedMods().get(i);
                    try {
                        Blaze3D.openUri((URI)new URI(mod.changelogLink));
                        continue;
                    }
                    catch (URISyntaxException e) {
                        XaeroLib.LOGGER.error("suppressed exception", (Throwable)e);
                    }
                }
            }).bounds(this.width / 2 - 100, this.height / 6 + 120, 200, 20).build());
        }
        this.layout.addChild((LayoutElement)Button.builder((Component)Component.translatable((String)"Don't show again for these updates", (Object[])new Object[0]), b -> {
            for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                PatreonMod mod = (PatreonMod)Patreon.getOutdatedMods().get(i);
                if (mod.onVersionIgnore == null) continue;
                mod.onVersionIgnore.run();
            }
            this.minecraft.gui.setScreen(null);
        }).bounds(this.width / 2 - 100, this.height / 6 + 144, 200, 20).build());
        XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().initialize(this, this.width, this.height);
    }

    private static void confirmResult(boolean p_confirmResult_1_) {
        if (p_confirmResult_1_) {
            boolean shouldExit = false;
            if (Patreon.getHasAutoUpdates()) {
                for (GuiEventListener b : Minecraft.getInstance().gui.screen().children()) {
                    if (!(b instanceof Button)) continue;
                    ((Button)b).active = false;
                }
                shouldExit = GuiUpdateAll.autoUpdate();
            } else {
                shouldExit = true;
                for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                    PatreonMod m = (PatreonMod)Patreon.getOutdatedMods().get(i);
                    try {
                        Blaze3D.openUri((URI)new URI(m.changelogLink));
                        if (m.modJar == null) continue;
                        Blaze3D.openPath((Path)m.modJar.getParentFile().toPath());
                        continue;
                    }
                    catch (Exception e) {
                        XaeroLib.LOGGER.error("suppressed exception", (Throwable)e);
                        shouldExit = false;
                    }
                }
            }
            if (shouldExit) {
                Minecraft.getInstance().stop();
            } else {
                Minecraft.getInstance().gui.setScreen(null);
            }
            Minecraft.getInstance().stop();
        } else {
            Minecraft.getInstance().gui.setScreen(null);
        }
    }

    private static void download(BufferedOutputStream output, InputStream input, boolean closeInput) throws IOException {
        int read;
        byte[] buffer = new byte[256];
        while ((read = input.read(buffer, 0, buffer.length)) >= 0) {
            output.write(buffer, 0, read);
        }
        output.flush();
        if (closeInput) {
            input.close();
        }
        output.close();
    }

    private static boolean autoUpdate() {
        try {
            MessageDigest digestMD5;
            try {
                digestMD5 = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e1) {
                XaeroLib.LOGGER.info("No algorithm for MD5.");
                return false;
            }
            PatreonMod autoupdater = (PatreonMod)Patreon.getMods().get("autoupdater30");
            String jarLink = autoupdater.changelogLink;
            String jarMD5 = autoupdater.latestVersionLayout;
            URL url = new URL(jarLink);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(900);
            conn.setConnectTimeout(900);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
            if (conn.getContentLengthLong() > 0x200000L) {
                throw new IOException("Input too long to trust!");
            }
            InputStream input = conn.getInputStream();
            input = new BufferedInputStream(input);
            DigestInputStream digestInput = new DigestInputStream(input, digestMD5);
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(Services.PLATFORM.getGameDir().resolve("xaero_autoupdater.jar").toFile()));
            GuiUpdateAll.download(output, digestInput, true);
            byte[] digest = digestMD5.digest();
            String fileMD5 = Hex.encodeHexString((byte[])digest);
            if (!jarMD5.equals(fileMD5)) {
                XaeroLib.LOGGER.info("Invalid autoupdater MD5: " + fileMD5);
                return false;
            }
            ArrayList<String> command = new ArrayList<String>();
            Path javaPath = new File(System.getProperty("java.home")).toPath().resolve("bin").resolve("java");
            command.add(javaPath.toString());
            command.add("-jar");
            command.add("./xaero_autoupdater.jar");
            command.add("6");
            command.add(Patreon.getUpdateLocation());
            for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                PatreonMod m = (PatreonMod)Patreon.getOutdatedMods().get(i);
                if (m.modJar == null) continue;
                int canonicalPathAttempts = 10;
                String jarPath = null;
                while (canonicalPathAttempts-- > 0) {
                    try {
                        jarPath = m.modJar.getCanonicalPath();
                        break;
                    }
                    catch (IOException ioe) {
                        XaeroLib.LOGGER.info("IO exception fetching the canonical path to the mod jar!");
                        if (canonicalPathAttempts == 0) {
                            throw ioe;
                        }
                        XaeroLib.LOGGER.error("suppressed exception", (Throwable)ioe);
                        XaeroLib.LOGGER.info("Retrying... (" + canonicalPathAttempts + ")");
                        try {
                            Thread.sleep(25L);
                        }
                        catch (InterruptedException interruptedException) {}
                    }
                }
                command.add(jarPath);
                command.add(m.latestVersionLayout);
                command.add(m.currentVersion.split("_")[1]);
                command.add(m.latestVersion);
                command.add(m.currentVersion.split("_")[0]);
                command.add(m.md5 == null ? "null" : m.md5);
            }
            XaeroLib.LOGGER.info(String.join((CharSequence)", ", command));
            Runtime.getRuntime().exec(command.toArray(new String[0]));
        }
        catch (IOException e) {
            XaeroLib.LOGGER.error("suppressed exception", (Throwable)e);
            return false;
        }
        return true;
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.extractBackground(guiGraphics, p_render_1_, p_render_2_, p_render_3_);
        int mouseX = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
        int mouseY = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
        XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().render(guiGraphics, this, this.width, this.height, mouseX, mouseY, this.minecraft.getWindow().getGuiScale());
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.extractRenderState(guiGraphics, p_render_1_, p_render_2_, p_render_3_);
        XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().renderTooltips(guiGraphics, (Screen)this, this.width, this.height, p_render_1_, p_render_2_, this.minecraft.getWindow().getGuiScale());
    }

    @Override
    public void addButtonVisible(AbstractWidget button) {
        this.addRenderableWidget((GuiEventListener)button);
    }

    @Override
    public <S extends Screen> S getScreen() {
        return (S)this;
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        XaeroLib.INSTANCE.getClient().getWidgetScreenHandler().handleClick((Screen)this, this.width, this.height, (int)event.x(), (int)event.y(), this.minecraft.getWindow().getGuiScale());
        return super.mouseClicked(event, doubleClick);
    }
}

