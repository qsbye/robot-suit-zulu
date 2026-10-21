/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;

public class RadarIconDefinitionReloader {
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public void reloadResources(Map<Identifier, RadarIconDefinition> iconDefinitions) {
        MinimapLogs.LOGGER.info("Reloading radar icon resources...");
        Set entityIds = BuiltInRegistries.ENTITY_TYPE.keySet();
        int attempts = 5;
        for (int i = 0; i < attempts; ++i) {
            try {
                this.reloadResourcesAttempt(iconDefinitions, this.gson, entityIds);
                break;
            }
            catch (IOException ioe) {
                if (i != attempts - 1) continue;
                throw new RuntimeException(ioe);
            }
        }
        MinimapLogs.LOGGER.info("Reloaded radar icon resources!");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void reloadResourcesAttempt(Map<Identifier, RadarIconDefinition> iconDefinitions, Gson gson, Set<Identifier> entityIds) throws IOException {
        iconDefinitions.clear();
        for (Identifier id : entityIds) {
            InputStream resourceInput = null;
            BufferedReader reader = null;
            String entityDefinitionJson = null;
            try {
                Resource resource;
                Optional oResource = Minecraft.getInstance().getResourceManager().getResource(Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)("entity/icon/definition/" + id.getNamespace() + "/" + id.getPath() + ".json")));
                if (!oResource.isPresent() || (resource = (Resource)oResource.get()) == null) continue;
                resourceInput = resource.open();
                reader = new BufferedReader(new InputStreamReader(resourceInput));
                StringBuilder stringBuilder = new StringBuilder();
                reader.lines().forEach(line -> {
                    stringBuilder.append((String)line);
                    stringBuilder.append('\n');
                });
                entityDefinitionJson = stringBuilder.toString();
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
                if (resourceInput == null) continue;
                resourceInput.close();
                continue;
            }
            try {
                RadarIconDefinition radarIconDefinition = (RadarIconDefinition)gson.fromJson(entityDefinitionJson, RadarIconDefinition.class);
                radarIconDefinition.construct(id);
                iconDefinitions.put(id, radarIconDefinition);
            }
            catch (JsonSyntaxException jse) {
                MinimapLogs.LOGGER.error("Json syntax exception when loading the radar icon definition for " + String.valueOf(id) + ".", (Throwable)jse);
            }
        }
    }
}

