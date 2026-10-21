/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import xaero.lib.XaeroLib;

public class ImageIOUtils {
    public static BufferedImage getImageThroughZipError(ImageInputStream input, String debugName) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
        if (!readers.hasNext()) {
            return null;
        }
        ImageReader reader = readers.next();
        reader.setInput(input);
        int imageIndex = 0;
        ImageReadParam param = reader.getDefaultReadParam();
        ImageTypeSpecifier type = reader.getImageTypes(0).next();
        int width = reader.getWidth(imageIndex);
        int height = reader.getHeight(imageIndex);
        BufferedImage image = type.createBufferedImage(width, height);
        param.setDestination(image);
        try {
            reader.read(imageIndex, param);
        }
        catch (IOException ioe) {
            if (!(ioe.getCause() instanceof ZipException)) {
                throw ioe;
            }
            XaeroLib.LOGGER.error("Suppressed ZIP exception loading PNG {}: {}", (Object)debugName, (Object)ioe.getCause().getMessage());
        }
        return image;
    }
}

