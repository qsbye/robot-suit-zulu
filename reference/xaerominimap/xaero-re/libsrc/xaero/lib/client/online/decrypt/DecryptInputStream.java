/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.online.decrypt;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import javax.crypto.Cipher;

public class DecryptInputStream
extends InputStream {
    private InputStream src;
    private Cipher cipher;
    private byte[] encryptedBuffer = new byte[256];
    private byte[] currentBlock;
    private int blockCount;
    private int blockOffset;
    private boolean endReached;
    private long prevExpirationTime = -1L;

    public DecryptInputStream(InputStream src, Cipher cipher) {
        this.src = src;
        this.cipher = cipher;
    }

    @Override
    public int read() throws IOException {
        if (this.endReached) {
            return -1;
        }
        if (this.currentBlock == null || this.currentBlock.length == this.blockOffset) {
            int read;
            for (int offset = 0; offset < 256; offset += read) {
                read = this.src.read(this.encryptedBuffer, offset, 256 - offset);
                if (read != -1) continue;
                this.endReached = true;
                if (offset == 0) {
                    throw new IOException("Online mod data missing confirmation block!");
                }
                throw new IOException("Encrypted block too short!");
            }
            try {
                this.currentBlock = this.cipher.doFinal(this.encryptedBuffer);
                long expirationTime = 0L;
                int blockIndex = 0;
                this.blockOffset = 0;
                while (this.blockOffset < 8) {
                    expirationTime |= (long)(this.currentBlock[this.blockOffset] & 0xFF) << 8 * this.blockOffset;
                    ++this.blockOffset;
                }
                for (int i = 0; i < 2; ++i) {
                    blockIndex |= (this.currentBlock[this.blockOffset] & 0xFF) << 8 * i;
                    ++this.blockOffset;
                }
                if (System.currentTimeMillis() > expirationTime) {
                    this.endReached = true;
                    throw new IOException("Online mod data expired! Date: " + String.valueOf(new Date(expirationTime)));
                }
                if (this.prevExpirationTime != -1L && expirationTime != this.prevExpirationTime) {
                    this.endReached = true;
                    throw new IOException("Online mod data expiration date mismatch! Dates: " + String.valueOf(new Date(expirationTime)) + " VS " + String.valueOf(new Date(this.prevExpirationTime)));
                }
                if (blockIndex != this.blockCount) {
                    this.endReached = true;
                    throw new IOException("Online mod data block index mismatch! " + blockIndex + " VS " + this.blockCount);
                }
                this.prevExpirationTime = expirationTime;
                ++this.blockCount;
                if (this.blockOffset == this.currentBlock.length) {
                    this.endReached = true;
                    return -1;
                }
            }
            catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return this.currentBlock[this.blockOffset++];
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.src.close();
        this.encryptedBuffer = null;
        this.currentBlock = null;
    }
}

