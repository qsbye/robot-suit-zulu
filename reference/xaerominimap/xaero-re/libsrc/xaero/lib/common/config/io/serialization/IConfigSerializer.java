/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.io.serialization;

import java.nio.file.Path;
import javax.annotation.Nullable;
import xaero.lib.common.config.Config;

public interface IConfigSerializer<C extends Config> {
    public String serialize(C var1, @Nullable Path var2);

    public C deserialize(String var1, boolean var2, String var3, @Nullable Path var4);
}

