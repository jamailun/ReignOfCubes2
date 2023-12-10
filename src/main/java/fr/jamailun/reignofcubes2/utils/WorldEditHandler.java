package fr.jamailun.reignofcubes2.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class WorldEditHandler {
    private WorldEditHandler() {}

    private static final WorldEdit worldEdit = WorldEdit.getInstance();

    public static @Nullable Region getSelectedRegion(String authorsName) {
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(authorsName);
        if(worldEditSession != null && worldEditSession.getSelectionWorld() != null) {
            RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
            if(regionSelector.isDefined()) {
                try {
                    return regionSelector.getRegion();
                } catch (IncompleteRegionException e) {
                    Bukkit.getLogger().severe("[WorldEditHandler] Failed to load zone: " + e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    public static @NotNull Block[] getSelectedBlocks(String authorsName) {
        Region region = getSelectedRegion(authorsName);
        if(region == null)
            return new Block[0];

        World world = Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName());
        Block[] blocks = new Block[(int) region.getVolume()];
        int i = 0;
        for(BlockVector3 blockVector3 : region) {
            blocks[i] = new Location(world, blockVector3.getX(), blockVector3.getY(), blockVector3.getZ()).getBlock();
            i++;
        }
        return blocks;
    }

}
