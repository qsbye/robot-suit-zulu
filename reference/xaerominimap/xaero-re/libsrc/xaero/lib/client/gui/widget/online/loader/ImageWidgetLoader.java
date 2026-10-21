/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.loader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.FileAttribute;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.codec.binary.Hex;
import xaero.lib.XaeroLib;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.graphics.util.TextureUtils;
import xaero.lib.client.gui.widget.online.ImageWidgetBuilder;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.loader.ScalableWidgetLoader;
import xaero.lib.common.util.InternetUtils;
import xaero.lib.platform.Services;

public class ImageWidgetLoader
extends ScalableWidgetLoader {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    public Widget load(Map<String, String> parsedArgs) throws IOException {
        block32: {
            builder = new ImageWidgetBuilder();
            this.commonLoad(builder, parsedArgs);
            image = parsedArgs.get("image");
            image_url = parsedArgs.get("image_url");
            textureId = null;
            if (image == null) break block32;
            if (!image.replaceAll("[^a-zA-Z0-9_]+", "").equals(image)) {
                XaeroLib.LOGGER.info("Invalid widget image id!");
                return null;
            }
            image_md5 = parsedArgs.get("image_md5");
            if (image_md5 == null) {
                XaeroLib.LOGGER.info("No image md5.");
                return null;
            }
            try {
                digestMD5 = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e1) {
                XaeroLib.LOGGER.info("No algorithm for MD5.");
                return null;
            }
            builder.setImageId(image);
            tex_base_level = parsedArgs.get("tex_base_level");
            tex_max_level = parsedArgs.get("tex_max_level");
            tex_min_lod = parsedArgs.get("tex_min_lod");
            tex_max_lod = parsedArgs.get("tex_max_lod");
            tex_lod_bias = parsedArgs.get("tex_lod_bias");
            tex_mag_filter = parsedArgs.get("tex_mag_filter");
            tex_min_filter = parsedArgs.get("tex_min_filter");
            tex_wrap_s = parsedArgs.get("tex_wrap_s");
            tex_wrap_t = parsedArgs.get("tex_wrap_t");
            cacheFolder = Services.PLATFORM.getGameDir().resolve("XaeroCache").toFile();
            cacheFolderPath = cacheFolder.toPath();
            if (!Files.exists(cacheFolderPath, new LinkOption[0])) {
                Files.createDirectories(cacheFolderPath, new FileAttribute[0]);
            }
            resourceLocation = Identifier.fromNamespaceAndPath((String)"xaerobetterpvp", (String)("gui/" + image + ".png"));
            inputStream = null;
            digestStream = null;
            bufferedImage = null;
            try {
                try {
                    resource = (Resource)Minecraft.getInstance().getResourceManager().getResource(resourceLocation).get();
                    inputStream = resource.open();
                    ** GOTO lbl80
                }
                catch (NoSuchElementException e) {
                    XaeroLib.LOGGER.info("Widget image not included in jar. Checking cache...");
                    cacheFilePath = cacheFolderPath.resolve(image + ".cache");
                    if (Files.exists(cacheFilePath, new LinkOption[0])) ** GOTO lbl78
                    XaeroLib.LOGGER.info("Widget image not in cache. Downloading...");
                    if (image_url == null) {
                        XaeroLib.LOGGER.info("No image URL.");
                        var25_29 = null;
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (digestStream != null) {
                            digestStream.close();
                        }
                        return var25_29;
                    }
                    url = new URL(image_url);
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setReadTimeout(900);
                    conn.setConnectTimeout(900);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
                    if (conn.getContentLengthLong() > 0x500000L) {
                        throw new IOException("Image too big to trust!");
                    }
                    input = null;
                    output = null;
                    try {
                        input = conn.getInputStream();
                        output = new BufferedOutputStream(new FileOutputStream(cacheFilePath.toFile()));
                        InternetUtils.download((BufferedOutputStream)output, input);
                    }
                    finally {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                    }
                    inputStream = new FileInputStream(cacheFilePath.toFile());
lbl80:
                    // 2 sources

                    inputStream = new BufferedInputStream(inputStream);
                    digestStream = new DigestInputStream(inputStream, digestMD5);
                    bufferedImage = ImageIO.read(digestStream);
                    while (digestStream.read() != -1) {
                    }
                    digest = digestMD5.digest();
                    fileMD5 = Hex.encodeHexString((byte[])digest);
                    if (!image_md5.equals(fileMD5)) {
                        XaeroLib.LOGGER.info("Invalid image MD5: " + fileMD5);
                        bufferedImage.flush();
                        bufferedImage = null;
                    }
                }
            }
            finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (digestStream != null) {
                    digestStream.close();
                }
            }
            if (bufferedImage == null) {
                return null;
            }
            imageW = bufferedImage.getWidth();
            imageH = bufferedImage.getHeight();
            builder.setImageW(imageW);
            builder.setImageH(imageH);
            texture = RenderSystem.getDevice().createTexture((String)null, 5, GpuFormat.RGBA8_UINT, imageW, imageH, 1, tex_max_level != null ? Integer.parseInt(tex_max_level) + 1 : 1);
            if (texture == null) {
                return null;
            }
            textureId = new GpuTextureAndView(texture, RenderSystem.getDevice().createTextureView(texture));
            builder.setGlTexture(textureId);
            minFilter = tex_min_filter != null ? Integer.parseInt(tex_min_filter) : 9729;
            magFilter = tex_mag_filter != null ? Integer.parseInt(tex_mag_filter) : 9728;
            minIsLinear = minFilter == 9729 || minFilter == 9985 || minFilter == 9987;
            magIsLinear = magFilter == 9729 || magFilter == 9985 || magFilter == 9987;
            usingMipmaps = minIsLinear != false && minFilter != 9729 || magIsLinear != false && magFilter != 9729;
            wrapS = tex_wrap_s != null ? Integer.parseInt(tex_wrap_s) : 33071;
            wrapT = tex_wrap_t != null ? Integer.parseInt(tex_wrap_t) : 33071;
            builder.setSampler(RenderSystem.getDevice().createSampler(wrapS == 33071 ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT, wrapT == 33071 ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT, minIsLinear != false ? FilterMode.LINEAR : FilterMode.NEAREST, magIsLinear != false ? FilterMode.LINEAR : FilterMode.NEAREST, 1, OptionalDouble.of(usingMipmaps != false ? 4.0 : 1.0)));
            baseBuffer = TextureUtils.allocateByteBuffer(imageW * imageH * 4, ByteOrder.BIG_ENDIAN);
            buffer = baseBuffer.asIntBuffer();
            for (y = 0; y < imageH; ++y) {
                for (x = 0; x < imageW; ++x) {
                    color = bufferedImage.getRGB(x, y);
                    alpha = color >> 24 & 255;
                    reformattedColor = color << 8 | alpha;
                    buffer.put(reformattedColor);
                }
            }
            buffer.flip();
            bufferedImage.flush();
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, baseBuffer, 0, 0, 0, 0, imageW, imageH);
        }
        if (builder.validate()) {
            return builder.build();
        }
        if (textureId != null) {
            textureId.close();
        }
        return null;
    }
}

