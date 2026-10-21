/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class CustomUniformValueType<T> {
    private final int size;
    private final BiConsumer<ByteBuffer, T> writer;
    private final BiPredicate<T, T> equationChecker;

    public CustomUniformValueType(int size, BiConsumer<ByteBuffer, T> writer) {
        this(size, writer, Objects::equals);
    }

    public CustomUniformValueType(int size, BiConsumer<ByteBuffer, T> writer, BiPredicate<T, T> equationChecker) {
        this.size = size;
        this.writer = writer;
        this.equationChecker = equationChecker;
    }

    public int getSize() {
        return this.size;
    }

    public BiConsumer<ByteBuffer, T> getWriter() {
        return this.writer;
    }

    public boolean checkEquation(T currentValue, T value) {
        return this.equationChecker.test(currentValue, value);
    }
}

