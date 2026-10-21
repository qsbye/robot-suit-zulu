/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.level;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import xaero.common.server.level.LevelMapProperties;

public class LevelMapPropertiesIO {
    public static final String FILE_NAME = "xaeromap.txt";

    public void load(Path file, LevelMapProperties dest) throws IOException {
        try (BufferedReader reader = null;){
            reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file.toFile()), "UTF8"));
            dest.read(reader);
        }
    }

    public void save(Path file, LevelMapProperties dest) throws IOException {
        try (BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(file.toFile()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter((OutputStream)bufferedOutput, StandardCharsets.UTF_8));){
            dest.write(writer);
        }
    }
}

