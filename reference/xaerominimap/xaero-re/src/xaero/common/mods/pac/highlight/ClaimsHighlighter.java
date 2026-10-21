/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods.pac.highlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.minimap.highlight.ChunkHighlighter;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.util.TextSplitter;
import xaero.pac.client.claims.api.IClientClaimsManagerAPI;
import xaero.pac.client.claims.api.IClientDimensionClaimsManagerAPI;
import xaero.pac.client.claims.player.api.IClientPlayerClaimInfoAPI;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.claims.player.api.IPlayerDimensionClaimsAPI;

public class ClaimsHighlighter
extends ChunkHighlighter {
    private final IClientClaimsManagerAPI claimsManager;
    private final IXaeroMinimap modMain;
    private final ClientConfigManager configManager;
    private List<Component> cachedTooltip;
    private IPlayerChunkClaimAPI cachedTooltipFor;
    private int cachedForWidth;
    private String cachedForCustomName;
    private int cachedForClaimsColor;
    private boolean cachedForAnchor;
    private boolean cachedPartyOwned;
    private Component cachedForPartyName;
    private Identifier cachedForDimensionId;
    private boolean cachedForModeratorMode;

    public ClaimsHighlighter(IXaeroMinimap modMain, IClientClaimsManagerAPI claimsManager) {
        super(true);
        this.modMain = modMain;
        this.configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        this.claimsManager = claimsManager;
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        IClientDimensionClaimsManagerAPI claimsDimension = this.claimsManager.getDimension(dimension.identifier());
        if (claimsDimension == null) {
            return false;
        }
        return claimsDimension.getRegion(regionX, regionZ) != null;
    }

    @Override
    protected int[] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!((Boolean)this.configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS)).booleanValue()) {
            return null;
        }
        IPlayerChunkClaimAPI currentClaim = this.claimsManager.get(dimension.identifier(), chunkX, chunkZ);
        if (currentClaim == null) {
            return null;
        }
        IClientPlayerClaimInfoAPI playerClaimInfoAPI = this.claimsManager.getPlayerInfo(currentClaim.getPlayerId());
        IPlayerDimensionClaimsAPI playerDimensionClaimsAPI = playerClaimInfoAPI.ensureDimension(dimension.identifier());
        boolean anchor = playerDimensionClaimsAPI.getAnchors().contains(new ChunkPos(chunkX, chunkZ));
        IPlayerChunkClaimAPI topClaim = anchor ? null : this.claimsManager.get(dimension.identifier(), chunkX, chunkZ - 1);
        IPlayerChunkClaimAPI rightClaim = anchor ? null : this.claimsManager.get(dimension.identifier(), chunkX + 1, chunkZ);
        IPlayerChunkClaimAPI bottomClaim = anchor ? null : this.claimsManager.get(dimension.identifier(), chunkX, chunkZ + 1);
        IPlayerChunkClaimAPI leftClaim = anchor ? null : this.claimsManager.get(dimension.identifier(), chunkX - 1, chunkZ);
        int claimColor = this.getClaimsColor(currentClaim, dimension.identifier());
        int claimColorFormatted = (claimColor & 0xFF) << 24 | (claimColor >> 8 & 0xFF) << 16 | (claimColor >> 16 & 0xFF) << 8;
        int borderOpacity = (Integer)this.configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY);
        int fillOpacity = (Integer)this.configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY);
        int centerColor = claimColorFormatted | 255 * fillOpacity / 100;
        int sideColor = claimColorFormatted | 255 * borderOpacity / 100;
        this.resultStore[0] = centerColor;
        this.resultStore[1] = topClaim != currentClaim ? sideColor : centerColor;
        this.resultStore[2] = rightClaim != currentClaim ? sideColor : centerColor;
        this.resultStore[3] = bottomClaim != currentClaim ? sideColor : centerColor;
        this.resultStore[4] = leftClaim != currentClaim ? sideColor : centerColor;
        return this.resultStore;
    }

    @Override
    public int[] getChunkHighlitColor(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        int[] colors = super.getChunkHighlitColor(dimension, chunkX, chunkZ);
        if (colors == null) {
            return null;
        }
        IPlayerChunkClaimAPI currentClaim = this.claimsManager.get(dimension.identifier(), chunkX, chunkZ);
        IClientPlayerClaimInfoAPI playerClaimInfoAPI = this.claimsManager.getPlayerInfo(currentClaim.getPlayerId());
        IPlayerDimensionClaimsAPI playerDimensionClaimsAPI = playerClaimInfoAPI.ensureDimension(dimension.identifier());
        if (!playerDimensionClaimsAPI.getAnchors().contains(new ChunkPos(chunkX, chunkZ))) {
            return colors;
        }
        int claimColor = this.getClaimsColor(currentClaim, dimension.identifier());
        int claimColorFormatted = (claimColor & 0xFF) << 24 | (claimColor >> 8 & 0xFF) << 16 | (claimColor >> 16 & 0xFF) << 8;
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        int borderOpacity = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY);
        int sideColor = claimColorFormatted | 255 * borderOpacity / 100;
        for (int i = 6; i < 10; ++i) {
            for (int j = 6; j < 10; ++j) {
                this.applyAnchorPixel(colors, i, j, sideColor);
            }
        }
        return colors;
    }

    private void applyAnchorPixel(int[] colors, int x, int z, int color) {
        int index = z << 4 | x;
        int originalColor = colors[index];
        colors[index] = color;
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return this.claimsManager.get(dimension.identifier(), chunkX, chunkZ) != null;
    }

    @Override
    public void addChunkHighlightTooltips(InfoDisplayCompiler compiler, ResourceKey<Level> dimension, int chunkX, int chunkZ, int width) {
        if (!((Boolean)this.configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.OPAC_CURRENT_CLAIM)).booleanValue()) {
            return;
        }
        Identifier dimensionId = dimension.identifier();
        IPlayerChunkClaimAPI currentClaim = this.claimsManager.get(dimensionId, chunkX, chunkZ);
        if (currentClaim == null) {
            return;
        }
        UUID currentClaimId = currentClaim.getPlayerId();
        IClientPlayerClaimInfoAPI claimInfo = this.claimsManager.getPlayerInfo(currentClaimId);
        String customName = this.getClaimsCustomName(currentClaim, dimensionId);
        int actualClaimsColor = this.getClaimsColor(currentClaim, dimensionId);
        int claimsColor = actualClaimsColor | 0xFF000000;
        boolean anchor = claimInfo.ensureDimension(dimensionId).getAnchors().contains(new ChunkPos(chunkX, chunkZ));
        boolean partyOwned = claimInfo.isPartyOwned();
        Component partyName = claimInfo.getPartyName();
        boolean moderatorMode = this.claimsManager.isModeratorMode();
        if (!(Objects.equals(currentClaim, this.cachedTooltipFor) && this.cachedForWidth == width && this.cachedForClaimsColor == claimsColor && Objects.equals(customName, this.cachedForCustomName) && anchor == this.cachedForAnchor && partyOwned == this.cachedPartyOwned && Objects.equals(partyName, this.cachedForPartyName) && dimensionId.equals((Object)this.cachedForDimensionId) && moderatorMode == this.cachedForModeratorMode)) {
            MutableComponent tooltip = Component.literal((String)(anchor ? "\u2693 " : "\u25a1 ")).withStyle(s -> s.withColor(claimsColor));
            tooltip.getSiblings().add(this.claimsManager.getFullName(currentClaim, dimensionId, !moderatorMode).copy().withStyle(ChatFormatting.WHITE));
            this.cachedTooltip = new ArrayList<Component>();
            TextSplitter.splitTextIntoLines(this.cachedTooltip, (int)width, (int)width, (FormattedText)tooltip, null);
            this.cachedTooltipFor = currentClaim;
            this.cachedForWidth = width;
            this.cachedForCustomName = customName;
            this.cachedForClaimsColor = claimsColor;
            this.cachedForAnchor = anchor;
            this.cachedPartyOwned = partyOwned;
            this.cachedForPartyName = partyName;
            this.cachedForDimensionId = dimensionId;
            this.cachedForModeratorMode = moderatorMode;
        }
        for (int i = 0; i < this.cachedTooltip.size(); ++i) {
            compiler.addLine(this.cachedTooltip.get(i));
        }
    }

    private String getClaimsCustomName(IPlayerChunkClaimAPI currentClaim, Identifier dimension) {
        return this.claimsManager.getCustomName(currentClaim, dimension);
    }

    private int getClaimsColor(IPlayerChunkClaimAPI currentClaim, Identifier dimension) {
        return this.claimsManager.getColor(currentClaim, dimension);
    }
}

